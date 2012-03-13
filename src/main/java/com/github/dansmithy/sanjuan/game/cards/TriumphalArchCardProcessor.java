package com.github.dansmithy.sanjuan.game.cards;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.CardProcessor;
import com.github.dansmithy.sanjuan.game.PlayerNumbers;
import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.Player;

@Named
public class TriumphalArchCardProcessor implements CardProcessor {

	private static final BuildingType.Card[] statues = { BuildingType.Card.Statue, BuildingType.Card.Statue, BuildingType.Card.VictoryColumn, BuildingType.Card.Hero };
	
	@Override
	public void doVictoryPoints(BuildingType building, List<BuildingType> buildings, Player player) {
		int statueCount = 0;
		for (BuildingType otherBuilding : buildings) {
			if (buildingIsAStatue(otherBuilding)) {
				statueCount += 1;
			}
		}
		int bonusPoints = statueCount == 0 ? 0 : statueCount*2 + 2;
		player.setVictoryPoints(player.getVictoryPoints() + bonusPoints);
	}

	private boolean buildingIsAStatue(BuildingType otherBuilding) {
		return Arrays.asList(statues).contains(otherBuilding.getCard());
	}

	@Override
	public String getProcessorType() {
		return "triumphalarch";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		// TODO Auto-generated method stub
		
	}

}
