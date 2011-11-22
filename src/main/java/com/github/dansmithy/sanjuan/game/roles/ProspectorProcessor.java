package com.github.dansmithy.sanjuan.game.roles;

import java.util.Arrays;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayOffered;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

@Named
public class ProspectorProcessor implements RoleProcessor {

	@Override
	public Role getRole() {
		return Role.PROSPECTOR;
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		// do nothing
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {

		Play play = gameUpdater.getCurrentPlay();
		Player player = gameUpdater.getCurrentPlayer();
		Game game = gameUpdater.getGame();
		
		// TODO change to based on privilege system
		if (gameUpdater.getCurrentPhase().getLeadPlayer().equals(player.getName())) {
			
			Integer prospectedCard = game.getDeck().takeOne();
			player.addToHand(prospectedCard);
			PlayOffered offered = play.createOffered();
			offered.setProspected(Arrays.asList(prospectedCard));
			gameUpdater.updateDeck(game.getDeck());
			gameUpdater.updatePlayer(player);
		}
		
//		gameUpdater.completedPlay(play, playChoice);
//		gameUpdater.createNextStep();
	}

}
