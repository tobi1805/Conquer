package org.jel.game.messages;

import org.jel.game.Messages;
import org.jel.game.data.City;

public record RandomEventMessage(RandomEvent randomEvent, double factorOfPeople, double factorOfSoldiers,
		double growthFactor, City city) implements Message {
	@Override
	public boolean isBadForPlayer() {
		if (!this.isPlayerInvolved()) {
			return false;
		}
		return switch (this.randomEvent) {
		case ACCIDENT:
		case CIVIL_WAR:
		case CROP_FAILURE:
		case FIRE:
		case PANDEMIC:
		case PESTILENCE:
		case REBELLION:
		case SABOTAGE:
			yield true;
		case ECONOMIC_GROWTH:
		case GROWTH:
		case MIGRATION:
			yield false;
		};
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.city.isPlayerCity();
	}

	@Override
	public String getMessageText() {
		return Messages.getMessage(this.randomEvent.getMessage(), this.city.getName());
	}
}
