package com.github.dansmithy.sanjuan.game.cards;

import java.util.List;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.CardProcessor;
import com.github.dansmithy.sanjuan.model.BuildingCategory;
import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.Player;

@Named
public class CityhallCardProcessor implements CardProcessor {

	@Override
	public void doVictoryPoints(BuildingType building, List<BuildingType> buildings, Player player) {
		int bonusPoints = 0;
		for (BuildingType otherBuilding : buildings) {
			if (otherBuilding.getCategory().equals(BuildingCategory.VIOLET)) {
				bonusPoints += 1;
			}
		}
		player.setVictoryPoints(player.getVictoryPoints() + bonusPoints);
	}

	@Override
	public String getProcessorType() {
		return "cityhall";
	}

}
