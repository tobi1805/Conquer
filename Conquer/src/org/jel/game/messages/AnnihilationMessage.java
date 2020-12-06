package org.jel.game.messages;

import org.jel.game.data.City;

public record AnnihilationMessage(City src, City destination, long numberOfAttackers) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.src.isPlayerCity() || this.destination.isPlayerCity();
	}

	@Override
	public String getMessageText() {
		return this.src.getName() + " attacked " + this.destination.getName() + " with " + this.numberOfAttackers
				+ "soldiers. They annihilated each others";
	}
}
