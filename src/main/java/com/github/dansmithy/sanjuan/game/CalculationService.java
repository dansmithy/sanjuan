package com.github.dansmithy.sanjuan.game;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GameState;
import com.github.dansmithy.sanjuan.model.GovernorPhase;
import com.github.dansmithy.sanjuan.model.Phase;
import com.github.dansmithy.sanjuan.model.PhaseState;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Round;
import com.github.dansmithy.sanjuan.model.RoundState;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.security.AuthenticatedSessionProvider;

@Named
public class CalculationService {

	private final CardFactory cardFactory;
	private final CardProcessorProvider cardProcessorProvider;
	private final AuthenticatedSessionProvider authenticatedSessionProvider;

	@Inject
	public CalculationService(CardFactory cardFactory, CardProcessorProvider cardProcessorProvider, AuthenticatedSessionProvider authenticatedSessionProvider) {
		this.cardFactory = cardFactory;
		this.cardProcessorProvider = cardProcessorProvider;
		this.authenticatedSessionProvider = authenticatedSessionProvider;
	}
	
	public void processPlayer(Player player) {
		
		if (authenticatedSessionProvider.getAuthenticatedUsername().equals(player.getName())) {
			player.setAuthenticatedPlayer(true);
		}
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

	public void setAuthenicatedUser(Game game) {
		if (game.getState().equals(GameState.PLAYING)) {
			Round currentRound = game.getCurrentRound();
			if (currentRound.getState().equals(RoundState.GOVERNOR)) {
				GovernorPhase governorPhase = currentRound.getGovernorPhase();
				governorPhase.setAuthenticatedPlayer(authenticatedSessionProvider.getAuthenticatedUsername().equals(governorPhase.getCurrentStepHidden().getPlayerName()));
			} else if (currentRound.getState().equals(RoundState.PLAYING) && currentRound.getCurrentPhase().getState().equals(PhaseState.PLAYING)) {
				Phase currentPhase = currentRound.getCurrentPhase(); 
				currentPhase.setAuthenticatedPlayer(authenticatedSessionProvider.getAuthenticatedUsername().equals(currentPhase.getCurrentPlayHidden().getPlayer()));
			}
		}
	}
}
