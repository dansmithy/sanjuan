package com.github.dansmithy.sanjuan.game.cards;

import java.util.List;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.CardProcessor;
import com.github.dansmithy.sanjuan.game.PlayerNumbers;
import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.Player;

@Named
public class StandardCardProcessor implements CardProcessor {

	@Override
	public void doVictoryPoints(BuildingType building, List<BuildingType> buildings, Player player) {
		player.setVictoryPoints(player.getVictoryPoints() + building.getVictoryPoints());
	}

	@Override
	public String getProcessorType() {
		return "standard";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		// TODO Auto-generated method stub
		
	}

}
