package com.github.dansmithy.sanjuan.game.roles;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

@Named
public class BuilderProcessor implements RoleProcessor {

	@Override
	public Role getRole() {
		return Role.BUILDER;
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		// do nothing
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {

		Play play = gameUpdater.getCurrentPlay();
		play.makePlay(playChoice);
		
		gameUpdater.completedPlay(play);
		
		Game game = gameUpdater.getGame();
		
		Player player = gameUpdater.getCurrentPlayer();
		player.moveToBuildings(playChoice.getBuild());
		player.removeHandCards(playChoice.getPaymentAsArray());
		gameUpdater.updatePlayer(player);

		game.getDeck().discard(playChoice.getPaymentAsArray());
		gameUpdater.updateDeck(game.getDeck());
		
		gameUpdater.createNextStep();		
		
	}


}
