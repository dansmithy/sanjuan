package com.github.dansmithy.sanjuan.game.roles;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.exception.IllegalGameStateException;
import com.github.dansmithy.sanjuan.exception.PlayChoiceInvalidException;
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
		
		verifyPlay(player, play, playChoice);
		
		gameUpdater.completedPlay(play, playChoice);
		
		player.moveToBuildings(playChoice.getBuild());
		player.removeHandCards(playChoice.getPayment());
		deck.discard(playChoice.getPayment());

		// TODO think this is incorrect. carpenter needs to be applied BEFORE poor house. this would do the reverse.
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
		}		
	}

	private void verifyPlay(Player player, Play play, PlayChoice playChoice) {
		if (!player.getHand().contains(playChoice.getBuild())) {
			throw new PlayChoiceInvalidException("Cannot build as build choice is not one you own.", PlayChoiceInvalidException.NOT_OWNED_BUILD_CHOICE);
		}
		
		for (Integer paymentCard : playChoice.getPayment()) {
			if (!player.getHand().contains(paymentCard)) {
				throw new PlayChoiceInvalidException(String.format("Cannot pay with card %d as is not one you own.", paymentCard), PlayChoiceInvalidException.NOT_OWNED_PAYMENT);
			}
		}
		
		int cost = calculateCost(playChoice.getBuild(), play.getOffered());
		if (playChoice.getPayment().size() != cost) {
			String exceptionType = playChoice.getPayment().size() < cost ? PlayChoiceInvalidException.UNDERPAID : PlayChoiceInvalidException.OVERPAID;
			throw new PlayChoiceInvalidException(String.format("Cost is %d, but %d cards have been offered as payment.", cost, playChoice.getPayment().size()), exceptionType);
		}
		
		if (cardFactory.getBuildingType(playChoice.getBuild()).isVioletBuilding()) {
			if (buildingIsAlreadyInList(playChoice.getBuild(), player.getBuildings())) {
				throw new IllegalGameStateException(String.format("Cannot build as already have that violet building in your buildings."), IllegalGameStateException.BUILDING_ALREADY_BUILT);
			}
		}
	}

	private boolean buildingIsAlreadyInList(Integer building, List<Integer> buildings) {
		return cardFactory.getBuildings(buildings).contains(cardFactory.getBuildingType(building));
	}

	private int calculateCost(Integer build, PlayOffered offered) {
		int defaultCost = cardFactory.getBuildingType(build).getBuildingCost();
		int discount = cardFactory.getBuildingType(build).isVioletBuilding() ? offered.getBuilderDiscountOnViolet() : offered.getBuilderDiscountOnProduction();
		return defaultCost - discount;
	}


}
