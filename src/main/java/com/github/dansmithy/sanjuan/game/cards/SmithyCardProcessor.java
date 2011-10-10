package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class SmithyCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "smithy";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.addBuilderDiscountOnProduction();
	}

}
