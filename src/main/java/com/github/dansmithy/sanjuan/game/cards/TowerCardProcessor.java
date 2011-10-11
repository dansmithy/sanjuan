package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class TowerCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "tower";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.addCardsCanHold(5);
	}

}
