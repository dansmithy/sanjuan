package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class LibraryCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "library";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.setHasLibrary();
	}

}
