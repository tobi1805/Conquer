package org.jel.game.data;

import java.awt.Color;
import java.util.List;

import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.plugins.PluginInterface;

/**
 * Another interface providing information.
 */
public interface ConquerInfo extends StrategyObject, PluginInterface {
	
	void addContext(GlobalContext context);
	
	void init();
	
	void executeActions();
	
	Clan getClan(int index);

	List<String> getClanNames();

	List<Double> getCoins();

	List<Color> getColors();

	default boolean isDead(Clan clan) {
		return this.isDead(clan.getId());
	}

	boolean isDead(int clan);

	default long maximumNumberOfSoldiersToRecruit(final Clan clan, final long limit) {
		return this.maximumNumberOfSoldiersToRecruit(clan.getId(), limit);
	}

	long maximumNumberOfSoldiersToRecruit(final int clan, final long limit);

	default void upgradeDefenseFully(final Clan clan, final City city) {
		this.upgradeDefenseFully(clan.getId(), city);
	}

	void upgradeDefenseFully(final int clan, final City city);

	default void upgradeResourceFully(final Clan clan, final Resource resources, final City city) {
		this.upgradeResourceFully(clan.getId(), resources, city);
	}

	void upgradeResourceFully(final int clan, final Resource resources, final City city);
}
