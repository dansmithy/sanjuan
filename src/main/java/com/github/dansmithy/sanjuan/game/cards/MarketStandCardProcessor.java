package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class MarketStandCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "markethall";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.registerTraderBonusCard(1, 1);
	}

}
