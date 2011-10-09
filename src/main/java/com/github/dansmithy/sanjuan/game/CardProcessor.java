package com.github.dansmithy.sanjuan.game;

import java.util.List;

import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.Player;

public interface CardProcessor {

	void doVictoryPoints(BuildingType building, List<BuildingType> buildings, Player player);
	
	String getProcessorType();

	void determinePrivileges(PlayerNumbers privileges);
}
