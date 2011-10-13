package com.github.dansmithy.sanjuan.game.roles;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;
import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Game;
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
		Game game = gameUpdater.getGame();
		Deck deck = game.getDeck();
		Player player = gameUpdater.getCurrentPlayer();
		PlayerNumbers numbers = player.getPlayerNumbers();
		
		gameUpdater.completedPlay(play, playChoice);
		
		player.moveToBuildings(playChoice.getBuild());
		player.removeHandCards(playChoice.getPayment());
		deck.discard(playChoice.getPayment());

		int bonusCardsCount = numbers.getBonusCardMatches(player, Role.BUILDER);
		BuildingType type = cardFactory.getBuildingType(playChoice.getBuild());
		if (type.isVioletBuilding()) {
			bonusCardsCount += numbers.getBuilderBonusOnViolet();
		}
		List<Integer> bonusCards = deck.take(bonusCardsCount);
		playChoice.setBonusCards(bonusCards);
		player.addToHand(bonusCards);
		
		gameUpdater.updatePlayer(player);
		gameUpdater.updateDeck(deck);
		
		gameUpdater.createNextStep();
		
		if (!gameUpdater.isPhaseChanged()) {
			initiateNewPlay(gameUpdater);
		} else {
			if (game.hasReachedEndCondition()) {
				game.markCompleted();
				gameUpdater.updateGameState();
			}
		}
		
	}


}
