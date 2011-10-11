package com.github.dansmithy.sanjuan.game.roles;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;
import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayOffered;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

@Named
public class BuilderProcessor implements RoleProcessor {

	private final CardFactory cardFactory;

	@Inject
	public BuilderProcessor(CardFactory cardFactory) {
		this.cardFactory = cardFactory;
	}
	
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
		PlayerNumbers numbers = player.getPlayerNumbers();
		
		gameUpdater.completedPlay(play, playChoice);
		
		player.moveToBuildings(playChoice.getBuild());
		player.removeHandCards(playChoice.getPaymentAsArray());
		deck.discard(playChoice.getPaymentAsArray());

		int bonusCards = numbers.getBonusCardMatches(player, Role.BUILDER);
		BuildingType type = cardFactory.getBuildingType(playChoice.getBuild());
		if (type.isVioletBuilding()) {
			bonusCards += numbers.getBuilderBonusOnViolet();
		}
		player.addToHand(deck.take(bonusCards));
		
		gameUpdater.updatePlayer(player);
		gameUpdater.updateDeck(deck);
		
		gameUpdater.createNextStep();
		
		if (!gameUpdater.isPhaseChanged()) {
			initiateNewPlay(gameUpdater);
		}		
		
	}


}
