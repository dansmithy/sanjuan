package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

@Named
public class AqueductCardProcessor extends StandardCardProcessor {

	@Override
	public String getProcessorType() {
		return "aqueduct";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.addGoodCanProduce();
	}

}
