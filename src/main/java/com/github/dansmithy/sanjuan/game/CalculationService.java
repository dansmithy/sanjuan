package com.github.dansmithy.sanjuan.game;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;

@Named
public class CalculationService {

	private final CardFactory cardFactory;
	private final CardProcessorProvider cardProcessorProvider;

	@Inject
	public CalculationService(CardFactory cardFactory, CardProcessorProvider cardProcessorProvider) {
		this.cardFactory = cardFactory;
		this.cardProcessorProvider = cardProcessorProvider;
	}
	
	public void processPlayer(Player player) {
		
		List<BuildingType> buildingList = cardFactory.getBuildingTypes(player.getBuildings());
		Collections.sort(buildingList, new Comparator<BuildingType>() {

			@Override
			public int compare(BuildingType first, BuildingType second) {
				return first.getComputeOrder().compareTo(second.getComputeOrder());
			}
			
		});
		
		player.setVictoryPoints(0);
		PlayerNumbers privileges = new PlayerNumbers();
		player.setPlayerNumbers(privileges);
		for (BuildingType building : buildingList) {
			CardProcessor processor = cardProcessorProvider.getProcessor(building.getProcessorType());
			processor.doVictoryPoints(building, buildingList, player);
			processor.determinePrivileges(privileges);
		}
	}
	
	public void processPlayers(List<Player> players) {
		for (Player player : players) {
			processPlayer(player);
		}
	}

}
