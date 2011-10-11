package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class MarketHallCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "marketstand";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.registerTraderBonusCard(2, 1);
	}

}
