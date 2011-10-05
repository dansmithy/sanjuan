package com.github.dansmithy.sanjuan.game.cards;

import java.util.List;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.CardProcessor;
import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.Player;

@Named
public class ChapelCardProcessor implements CardProcessor {

	@Override
	public void doVictoryPoints(BuildingType building, List<BuildingType> buildings, Player player) {
		int bonusPoints = building.getVictoryPoints();
		bonusPoints += player.getChapelCards().size();
		player.setVictoryPoints(player.getVictoryPoints() + bonusPoints);
	}

	@Override
	public String getProcessorType() {
		return "chapel";
	}

}
