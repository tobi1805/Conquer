import org.jel.game.data.ConquerInfoReaderFactory;
import org.jel.game.data.DefaultScenarioProvider;
import org.jel.game.data.InstalledScenarioProvider;
import org.jel.game.data.builtin.DefensiveStrategyProvider;
import org.jel.game.data.builtin.ModerateStrategyProvider;
import org.jel.game.data.builtin.OffensiveStrategyProvider;
import org.jel.game.data.builtin.RandomStrategyProvider;
import org.jel.game.data.ri.ScenarioFileReaderFactory;
import org.jel.game.data.strategy.StrategyProvider;

/**
 * The module exporting all required packages.
 */
module org.jel.game {
	requires transitive java.desktop;
	requires static org.junit.jupiter.api;

	exports org.jel.game.init;
	exports org.jel.game.data;
	exports org.jel.game.utils;
	exports org.jel.game.plugins;
	exports org.jel.game.data.strategy;
	exports org.jel.game.messages;

	uses org.jel.game.data.InstalledScenarioProvider;
	uses org.jel.game.data.strategy.StrategyProvider;
	uses org.jel.game.plugins.Plugin;
	uses org.jel.game.data.ConquerInfoReaderFactory;
	uses org.jel.game.init.InitTask;

	provides ConquerInfoReaderFactory with ScenarioFileReaderFactory;
	provides StrategyProvider
			with OffensiveStrategyProvider, DefensiveStrategyProvider, ModerateStrategyProvider, RandomStrategyProvider;
	provides InstalledScenarioProvider with DefaultScenarioProvider;
}