package com.github.dansmithy.sanjuan.game.roles;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.exception.PlayChoiceInvalidException;
import com.github.dansmithy.sanjuan.exception.SanJuanUnexpectedException;
import com.github.dansmithy.sanjuan.game.PlayerNumbers;
import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Phase;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.Tariff;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayOffered;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

@Named
public class TraderProcessor implements RoleProcessor {

	private final CardFactory cardFactory;

	@Inject
	public TraderProcessor(CardFactory cardFactory) {
		this.cardFactory = cardFactory;
	}
	
	@Override
	public Role getRole() {
		return Role.TRADER;
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		Play play = gameUpdater.getNewPlay();
		Game game = gameUpdater.getGame();
		Deck deck = game.getDeck();
		PlayerNumbers numbers = gameUpdater.getNewPlayer().getPlayerNumbers();
		
		if (gameUpdater.isCreatingFirstPlay()) {
			int currentTariff = game.getCurrentTariff();
			Tariff tariff = game.getTariffs().get(currentTariff);
			int nextTariff = nextTariff(currentTariff, game.getTariffs().size());
			game.setCurrentTariff(nextTariff);
			Phase phase = gameUpdater.getNewPhase();
			phase.setTariff(tariff);
			gameUpdater.updateCurrentTariff(nextTariff);
		}
		
		boolean withPrivilege = play.isHasPrivilige();
		int numberOfGoodsCanTrade = numbers.getTotalGoodsCanTrade(withPrivilege);

		PlayOffered offered = play.createOffered();
		offered.setGoodsCanTrade(numberOfGoodsCanTrade);
		gameUpdater.updateDeck(deck);
	}

	private int nextTariff(int currentTariff, int size) {
		currentTariff++;
		if (currentTariff == size) {
			currentTariff = 0;
		}
		return currentTariff;
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {
		Play play = gameUpdater.getCurrentPlay();
		Game game = gameUpdater.getGame();
		Deck deck = game.getDeck();
		Player player = gameUpdater.getCurrentPlayer();
		PlayerNumbers numbers = player.getPlayerNumbers();
		
		for (Integer chosenFactory : playChoice.getProductionFactories()) {
			
			if (!player.getBuildings().contains(chosenFactory)) {
				throw new PlayChoiceInvalidException(String.format("Cannot trade on factory %d as is not one of your buildings.", chosenFactory), PlayChoiceInvalidException.NOT_OWNED_FACTORY);
			}
			if (!player.getGoods().containsKey(chosenFactory)) {
				throw new PlayChoiceInvalidException(String.format("There is not a good to trade on card %d.", chosenFactory), PlayChoiceInvalidException.NOT_FULL_FACTORY);
			}
			int price = calculatePrice(chosenFactory, gameUpdater.getCurrentPhase().getTariff());
			Integer good = player.getGoods().remove(chosenFactory);
			deck.discard(good);
			player.addToHand(deck.take(price));
		}
		
		int goodsSold = playChoice.getProductionFactories().size();
		int bonusCardCount = numbers.getTraderBonusCards(goodsSold);
		List<Integer> bonusCards = deck.take(bonusCardCount);
		player.addToHand(bonusCards);
		
		gameUpdater.updateDeck(game.getDeck());
		gameUpdater.updatePlayer(player);
		gameUpdater.completedPlay(play, playChoice);
		gameUpdater.createNextStep();
		
		if (!gameUpdater.isPhaseChanged()) {
			initiateNewPlay(gameUpdater);
		}
	}

	private int calculatePrice(Integer factory, Tariff tariff) {
		return tariff.getPrices()[cardFactory.getBuildingType(factory).getBuildingCost()-1];
	}



}
