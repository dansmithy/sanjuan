package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class ArchiveCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "archive";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.councillorCanDiscardHandCards();
	}

}
