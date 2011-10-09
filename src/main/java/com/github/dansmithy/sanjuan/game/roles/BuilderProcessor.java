package com.github.dansmithy.sanjuan.game.roles;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;
import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayOffered;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

@Named
public class BuilderProcessor implements RoleProcessor {

	@Override
	public Role getRole() {
		return Role.BUILDER;
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		
		Play play = gameUpdater.getNewPlay();
		Deck deck = gameUpdater.getGame().getDeck();
		PlayerNumbers numbers = gameUpdater.getNewPlayer().getPlayerNumbers();
		
		boolean withPrivilege = play.isHasPrivilige();
		
		PlayOffered offered = play.createOffered();
		offered.setBuilderDiscountOnProduction(numbers.getTotalBuilderDiscountOnProduction(withPrivilege));
		offered.setBuilderDiscountOnViolet(numbers.getTotalBuilderDiscountOnViolet(withPrivilege));
		
		gameUpdater.updateDeck(deck);			
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {

		Play play = gameUpdater.getCurrentPlay();
		Deck deck = gameUpdater.getGame().getDeck();
		Player player = gameUpdater.getCurrentPlayer();
		
		gameUpdater.completedPlay(play, playChoice);
		
		player.moveToBuildings(playChoice.getBuild());
		player.removeHandCards(playChoice.getPaymentAsArray());
		gameUpdater.updatePlayer(player);

		deck.discard(playChoice.getPaymentAsArray());
		gameUpdater.updateDeck(deck);
		
		gameUpdater.createNextStep();		
		
	}


}
