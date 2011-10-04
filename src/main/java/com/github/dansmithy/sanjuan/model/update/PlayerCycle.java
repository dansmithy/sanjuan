package com.github.dansmithy.sanjuan.model.update;

import java.util.List;

public class PlayerCycle {

	private List<String> playerNames;

	public PlayerCycle(List<String> playerNames) {
		super();
		this.playerNames = playerNames;
	}

	public String next(String currentPlayer) {
		int index = playerNames.indexOf(currentPlayer);
		index++;
		if (index >= playerNames.size()) {
			index = 0;
		}
		return playerNames.get(index);
	}
	
	
}
