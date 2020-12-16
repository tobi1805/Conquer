package org.jel.game.messages;

import org.jel.game.Messages;
import org.jel.game.data.Clan;
import org.jel.game.data.Shared;

public record ExtinctionMessage(Clan clan) implements Message {

	@Override
	public String getMessageText() {
		return Messages.getMessage("Message.extincted", clan.getName());
	}

	@Override
	public boolean isBadForPlayer() {
		return clan.getId() == Shared.PLAYER_CLAN;
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.isBadForPlayer();
	}

}
