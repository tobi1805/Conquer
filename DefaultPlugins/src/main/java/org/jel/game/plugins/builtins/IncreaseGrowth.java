package org.jel.game.plugins.builtins;

import org.jel.game.data.ICity;
import org.jel.game.data.StreamUtils;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.Plugin;
import org.jel.game.utils.Graph;

public final class IncreaseGrowth implements Plugin {

	private static final double MAXIMUM_INCREASE = 0.1;

	@Override
	public String getName() {
		return "IncreaseGrowth";
	}

	@Override
	public void handle(final Graph<ICity> cities, final Context ctx) {
		StreamUtils.forEach(cities, a -> {
			if (a.getGrowth() < 1) {
				a.setGrowth(a.getGrowth() * (1 + (Math.random() % IncreaseGrowth.MAXIMUM_INCREASE)));
			}
		});
	}

}