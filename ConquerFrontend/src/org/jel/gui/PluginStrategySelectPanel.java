package org.jel.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jel.game.data.ConquerInfo;
import org.jel.game.data.GlobalContext;
import org.jel.game.data.strategy.StrategyProvider;
import org.jel.game.plugins.Plugin;

final class PluginStrategySelectPanel extends JPanel {
	private transient GlobalContext context;
	private final List<JCheckBox> plugins = new ArrayList<>();
	private final List<JCheckBox> strategies = new ArrayList<>();
	private final ConquerInfo info;

	PluginStrategySelectPanel(final GlobalContext context, final ConquerInfo info) {
		this.context = context;
		this.info = info;
		this.init();
	}

	private void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		final var pluginPanel = new JPanel();
		pluginPanel.setLayout(new BoxLayout(pluginPanel, BoxLayout.Y_AXIS));
		this.context.getPlugins().forEach(a -> {
			final var jb = new JCheckBox(a.getName(), true);
			pluginPanel.add(jb);
			jb.setEnabled(this.info.requiredPlugins().stream().filter(b -> b == a.getClass()).count() == 0);
			this.plugins.add(jb);
		});
		final var pluginScrollPanel = new JScrollPane(pluginPanel);
		this.add(pluginScrollPanel);
		final var strategiesPanel = new JPanel();
		strategiesPanel.setLayout(new BoxLayout(strategiesPanel, BoxLayout.Y_AXIS));
		this.context.getStrategies().forEach(a -> {
			// Skip builtin strategies
			final var module = a.getClass().getModule();
			final var name = module == null ? null : module.getName();
			if ((module != null) && "org.jel.game".equals(name)) {
				return;
			}
			final var jb = new JCheckBox(a.getName(), true);
			// If the strategy is required, set it to disabled
			jb.setEnabled(this.info.requiredStrategyProviders().stream().filter(b -> b == a.getClass()).count() == 0);
			strategiesPanel.add(jb);
			this.strategies.add(jb);
		});
		this.add(strategiesPanel);
	}

	GlobalContext modifyContext() {
		final List<Plugin> newPlugins = new ArrayList<>();
		this.plugins.forEach(a -> {
			if (!a.isSelected()) {
				return;
			}
			for (final var oldPlugin : this.context.getPlugins()) {
				if (a.getText().equals(oldPlugin.getName())) {
					newPlugins.add(oldPlugin);
				}
			}
		});
		final var pluginNames = newPlugins.stream().map(a -> a.getClass().getName()).collect(Collectors.toList());
		this.context.getPlugins().clear();
		this.context.getPlugins().addAll(newPlugins);
		this.context.getPluginNames().clear();
		this.context.getPluginNames().addAll(pluginNames);
		final List<StrategyProvider> newStrategies = new ArrayList<>();
		this.strategies.forEach(a -> {
			if (!a.isSelected()) {
				return;
			}
			for (final var oldStrategy : this.context.getStrategies()) {
				if (a.getText().equals(oldStrategy.getName())) {
					newStrategies.add(oldStrategy);
				}
			}
		});
		this.context.getStrategies().forEach(a -> {
			final var module = a.getClass().getModule();
			final var name = module == null ? null : module.getName();
			if ((module != null) && "org.jel.game".equals(name)) {
				newStrategies.add(a);
			}
		});
		final var strategyNames = newStrategies.stream().map(a -> a.getClass().getName()).collect(Collectors.toList());
		this.context.getStrategies().clear();
		this.context.getStrategies().addAll(newStrategies);
		this.context.getStrategyNames().clear();
		this.context.getStrategyNames().addAll(strategyNames);
		return this.context;
	}

	private static final long serialVersionUID = 3909874317355075179L;

}