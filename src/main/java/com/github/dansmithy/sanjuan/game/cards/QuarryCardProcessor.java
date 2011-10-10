package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class QuarryCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "quarry";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.addBuilderDiscountOnViolet();
	}

}
