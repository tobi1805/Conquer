package conquer.plugins.builtins;

import conquer.data.EventList;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.plugins.Context;
import conquer.plugins.Plugin;
import conquer.plugins.PluginInterface;
import conquer.plugins.ResourceHook;
import conquer.utils.Graph;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResourceAnalyzer implements Plugin, ResourceHook {

	private EventList events;
	private int currentRound = 0;

	@Override
	public void analyzeStats(final ICity city, final List<Double> statistics, final IClan clan) {
		if (this.currentRound < Integer.getInteger("resource.analyzer.delay", 10)) {
			return;
		}
		final List<Double> killedSoldiers = new ArrayList<>();
		final List<Double> killedCivilians = new ArrayList<>();
		for (var idx = 0; idx < statistics.size(); idx++) {
			final var resources = clan.getResources();
			this.collectStatistics(statistics, resources, clan, idx);
			if (statistics.get(idx) < 0) {
				final double d = statistics.get(idx);
				final var numSoldiersToGetToZero = ((-d) / city.getInfo().getResourceUsage(clan).soldierUsage()[idx]);
				if (numSoldiersToGetToZero < city.getNumberOfSoldiers()) {
					killedSoldiers.add(numSoldiersToGetToZero);
					killedCivilians.add(0.0);
				} else {
					this.killedCiviliansUpdate(city, idx, killedSoldiers, killedCivilians, d);
				}
			} else {
				killedCivilians.add(0.0);
				killedSoldiers.add(0.0);
			}
		}
		Collections.sort(killedSoldiers);
		Collections.sort(killedCivilians);
		final var maxSoldiers = (long) Math.floor(killedSoldiers.get(killedSoldiers.size() - 1));
		final var maxCivilians = (long) Math.floor(killedCivilians.get(killedCivilians.size() - 1));
		this.killSoldiers(city, maxSoldiers, clan);
		this.killCivilians(city, maxCivilians, clan);
	}

	private void collectStatistics(final List<Double> statistics, final List<Double> resources, final IClan clan,
								   final int idx) {
		if (statistics.get(idx) < 0) {
			double saved = clan.getResources().get(idx);
			saved += statistics.get(idx);
			if (saved >= 0) {
				resources.set(idx, saved);
				statistics.set(idx, 0.0);
			} else {
				resources.set(idx, 0.0);
				statistics.set(idx, -(-statistics.get(0) - clan.getResources().get(idx)));
			}
		}
	}

	@Override
	public String getName() {
		return "ResourceAnalyzer";
	}

	@Override
	public void handle(final Graph<ICity> cities, final Context ctx) {
		this.currentRound++;

	}

	@Override
	public void init(final PluginInterface pi) {
		pi.addResourceHook(this);
		this.events = pi.getEventList();
	}

	private void killCivilians(final ICity city, final long maxCivilians, final IClan clan) {
		if (maxCivilians <= 0) {
			return;
		}
		final var tmp = city.getNumberOfPeople();
		city.setNumberOfPeople(city.getNumberOfPeople() - maxCivilians);
		this.events.add(new CiviliansDiedBecauseOfMissingResourcesMessage(maxCivilians, city, clan));
		final var tmp2 = city.getNumberOfPeople();
		final var tmp3 = tmp > 100 ? 100 : tmp2;
		city.setNumberOfPeople(tmp2 == 0 ? tmp3 : tmp2);
	}

	private void killedCiviliansUpdate(final ICity city, final int idx, final List<Double> killedSoldiers,
									   final List<Double> killedCivilians, final double d) {
		final var d1 = d + (city.getNumberOfSoldiers() + city.getInfo().getResourceUsage().soldierUsage()[idx]);
		final var numCiviliansToGetToZero = (-d1) / city.getInfo().getResourceUsage().personUsage()[idx];
		killedSoldiers.add((double) city.getNumberOfSoldiers());
		if (Double.isNaN(numCiviliansToGetToZero)) {
			killedCivilians.add(0.0);
		} else {
			killedCivilians.add(Math.min(city.getNumberOfPeople(), numCiviliansToGetToZero));
		}
	}

	private void killSoldiers(final ICity city, final long maxSoldiers, final IClan clan) {
		if (maxSoldiers <= 0) {
			return;
		}
		final var tmp = city.getNumberOfSoldiers();
		city.setNumberOfSoldiers(city.getNumberOfSoldiers() - maxSoldiers);
		this.events.add(new SoldiersDesertedBecauseOfMissingResourcesMessage(maxSoldiers, city, clan));
		final var tmp2 = city.getNumberOfSoldiers();
		final var tmp3 = tmp > 100 ? 100 : tmp2;
		city.setNumberOfSoldiers(tmp2 == 0 ? tmp3 : tmp2);
	}

	@Override
	public void resume(final PluginInterface game, final InputStream bytes) throws IOException {
		try (final var dis = new DataInputStream(bytes)) {
			this.currentRound = dis.readInt();
		}
		this.events = game.getEventList();
	}

	@Override
	public void save(final OutputStream outputStream) throws IOException {
		try (final var dos = new DataOutputStream(outputStream)) {
			dos.writeInt(this.currentRound);
		}
	}
}
