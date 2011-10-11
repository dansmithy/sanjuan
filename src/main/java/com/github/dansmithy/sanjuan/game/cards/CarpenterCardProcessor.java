package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class CarpenterCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "carpenter";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.addBuilderBonusOnViolet();
	}

}
