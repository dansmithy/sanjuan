package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class PrefectureCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "prefecture";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.addCouncillorRetainCard();
	}

}
