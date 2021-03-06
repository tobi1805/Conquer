package conquer.gui;

import conquer.data.ConquerInfo;
import conquer.data.IClan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * One of the panels in the JTabbedPane of {@link GameFrame}. It allows to see
 * the relationship to other clans and to improve it by sending gifts.
 */
final class RelationshipPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7720369284705527955L;
	private final transient ConquerInfo game;
	private final List<JLabel> labels;

	/**
	 * Construct a new RelationshipPanel
	 *
	 * @param game The source of the data.
	 */
	RelationshipPanel(final ConquerInfo game) {
		this.game = game;
		this.labels = new ArrayList<>();
	}

	/**
	 * Updates the information about the relationship to other clans.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		final var clans = this.game.getClans();
		for (var i = 1; i < clans.size(); i++) {
			final var clan = clans.get(i);
			final var label = this.labels.get(i - 1);
			label.setText(clan.getName() + ": "
					+ String.format("%.2f", this.game.getRelationship(this.game.getPlayerClan(), clan)));
			this.repaint();
		}
	}

	/**
	 * Initialises this component.
	 */
	void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		final var clans = this.game.getClans();
		for (final IClan iClan : clans) {
			if (iClan.isPlayerClan()) {
				continue;
			}
			final var text = iClan.getName();
			final var clanLabel = new JLabel(text);
			clanLabel.setOpaque(true);
			clanLabel.setForeground(iClan.getColor());
			clanLabel.setFont(clanLabel.getFont().deriveFont(22.0f));
			this.add(clanLabel);
			this.labels.add(clanLabel);
		}
		final var giftPanel = new GiftPanel(this.game);
		giftPanel.init();
		this.add(giftPanel);
		final var timer = new ExtendedTimer(Utils.getRefreshRate(), this);
		timer.start();
	}
}
