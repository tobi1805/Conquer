package org.jel.game.plugins.builtins;

import java.util.Random;

import org.jel.game.data.City;
import org.jel.game.data.Shared;
import org.jel.game.data.StreamUtils;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.Plugin;
import org.jel.game.utils.Graph;

public final class ChangeCitiesMinds implements Plugin {
	private static final double INSTANT_CLAN_CHANGE = 0.05;
	private static final int PROBABILITY_NO_CHANGE_OF_CLAN = 75;
	private final Random random = new Random();
	private Context context;

	private boolean evalClanChange(final double soldiersToCivilians, final City c, final byte otherClan) {
		if (this.random.nextInt(100) > 90) {
			if ((soldiersToCivilians < 0.15) && (Math.random() > 0.85)) {
				c.setClan(this.context.getClan(otherClan));
				c.setNumberOfPeople((long) (c.getNumberOfPeople() * Shared.randomPercentage(90, 98)));
				c.setNumberOfSoldiers((long) (c.getNumberOfSoldiers() * Shared.randomPercentage(45, 90)));
				return true;
			} else if ((soldiersToCivilians < 0.25) && (Math.random() > 0.9)) {
				c.setClan(this.context.getClan(otherClan));
				c.setNumberOfPeople((long) (c.getNumberOfPeople() * Shared.randomPercentage(60, 88)));
				c.setNumberOfSoldiers((long) (c.getNumberOfSoldiers() * Shared.randomPercentage(55, 90)));
				return true;
			} else if ((soldiersToCivilians < 0.35) && (Math.random() > 0.98)) {
				c.setClan(this.context.getClan(otherClan));
				c.setNumberOfPeople((long) (c.getNumberOfPeople() * Shared.randomPercentage(20, 60)));
				c.setNumberOfSoldiers((long) (c.getNumberOfSoldiers() * Shared.randomPercentage(80, 98)));
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "ChangeCitiesMinds";
	}

	@Override
	public void handle(final Graph<City> cities, final Context ctx) {
		this.context = ctx;
		StreamUtils.getCitiesAsStream(cities).forEach(c -> {
			if (StreamUtils.getCitiesAsStream(cities, c.getClan()).count() == 1) {
				return;
			}
			final var oldClan = c.getClan();
			final var neighbours = cities.getConnected(c);
			var canChangeClan = false;
			var otherClan = -1;
			for (final City city : neighbours) {
				if ((otherClan == city.getClan()) || (otherClan == -1)) {
					otherClan = city.getClan();
					canChangeClan = true;
				} else {
					canChangeClan = false;
					break;
				}
			}
			if ((!canChangeClan || (otherClan == c.getClan()))
					|| (this.random.nextInt(100) < ChangeCitiesMinds.PROBABILITY_NO_CHANGE_OF_CLAN)) {
				return;
			}
			var changedClan = false;
			var soldiersToCivilians = 1D;
			try {
				soldiersToCivilians = (double) c.getNumberOfSoldiers() / (double) c.getNumberOfPeople();
			} catch (final ArithmeticException ae) {
				soldiersToCivilians = 1;
				Shared.LOGGER.exception(ae);
			}
			if (soldiersToCivilians < ChangeCitiesMinds.INSTANT_CLAN_CHANGE) {
				c.setClan(ctx.getClan(otherClan));
				changedClan = true;
			} else {
				changedClan = this.evalClanChange(soldiersToCivilians, c, (byte) otherClan);
			}
			if (changedClan) {
				ctx.appendToEventList(new ClanChangeMessage(c, ctx.getClan(oldClan), ctx.getClan(c.getClan())));
			}
		});
	}
}
