package com.github.dansmithy.sanjuan.game.roles;

import java.util.ArrayList;
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
public class ProducerProcessor implements RoleProcessor {

	private final CardFactory cardFactory;

	@Inject
	public ProducerProcessor(CardFactory cardFactory) {
		this.cardFactory = cardFactory;
	}
	
	@Override
	public Role getRole() {
		return Role.PRODUCER;
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		Play play = gameUpdater.getNewPlay();
		Deck deck = gameUpdater.getGame().getDeck();
		PlayerNumbers numbers = gameUpdater.getNewPlayer().getPlayerNumbers();
		
		boolean withPrivilege = play.isHasPrivilige();
		int numberOfGoodsCanProduce = numbers.getTotalGoodsCanProduce(withPrivilege);
		
		List<Integer> factoriesCanProduce = determineFactoriesCanProduce(gameUpdater.getNewPlayer());
		
		PlayOffered offered = play.createOffered();
		offered.setGoodsCanProduce(numberOfGoodsCanProduce);
		offered.setFactoriesCanProduce(factoriesCanProduce);
		
		gameUpdater.updateDeck(deck);			
	}

	private List<Integer> determineFactoriesCanProduce(Player player) {
		List<Integer> factoriesCanProduce = new ArrayList<Integer>();
		for (Integer building : player.getBuildings()) {
			BuildingType type = cardFactory.getBuildingType(building);
			if (type.isProductionBuilding()) {
				if (!player.getGoods().keySet().contains(building)) {
					factoriesCanProduce.add(building);
				}
			}
		}
		return factoriesCanProduce;
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {
		Play play = gameUpdater.getCurrentPlay();
		Game game = gameUpdater.getGame();
		Deck deck = game.getDeck();
		Player player = gameUpdater.getCurrentPlayer();
		PlayerNumbers numbers = player.getPlayerNumbers();
		
		for (Integer chosenFactory : playChoice.getProductionFactories()) {
			// TODO verify factory is permitted
			Integer good = deck.takeOne();
			player.addGood(chosenFactory, good);
		}
		
		int goodsProduced = playChoice.getProductionFactories().size();
		int bonusCardCount = numbers.getProducerBonusCards(goodsProduced);
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


}
