package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class WellCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "well";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.registerProducerBonusCard(2, 1);
	}

}
