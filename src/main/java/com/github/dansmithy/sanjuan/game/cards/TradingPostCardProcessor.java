package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class TradingPostCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "tradingpost";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.addGoodCanTrade();
	}

}
