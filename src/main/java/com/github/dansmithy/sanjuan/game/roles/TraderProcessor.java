package com.github.dansmithy.sanjuan.game.roles;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.exception.SanJuanUnexpectedException;
import com.github.dansmithy.sanjuan.game.PlayerNumbers;
import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayOffered;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

@Named
public class TraderProcessor implements RoleProcessor {

	@Override
	public Role getRole() {
		return Role.TRADER;
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		Play play = gameUpdater.getNewPlay();
		Deck deck = gameUpdater.getGame().getDeck();
		PlayerNumbers numbers = gameUpdater.getNewPlayer().getPlayerNumbers();
		
		boolean withPrivilege = play.isHasPrivilige();
		int numberOfGoodsCanTrade = numbers.getTotalGoodsCanTrade(withPrivilege);
		
		PlayOffered offered = play.createOffered();
		offered.setGoodsCanTrade(numberOfGoodsCanTrade);
		
		gameUpdater.updateDeck(deck);			
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {
		Play play = gameUpdater.getCurrentPlay();
		Game game = gameUpdater.getGame();
		Deck deck = game.getDeck();
		Player player = gameUpdater.getCurrentPlayer();
		
		for (Integer chosenFactory : playChoice.getProductionFactories()) {
			if (!player.getGoods().containsKey(chosenFactory)) {
				throw new SanJuanUnexpectedException(String.format("There is not a good to trade on card %d.", chosenFactory));
			}
			Integer good = player.getGoods().remove(chosenFactory);
			deck.discard(good);
			player.addToHand(deck.takeOne());
		}
		
		gameUpdater.updateDeck(game.getDeck());
		gameUpdater.updatePlayer(player);
		gameUpdater.completedPlay(play, playChoice);
		gameUpdater.createNextStep();
		
		if (!gameUpdater.isPhaseChanged()) {
			initiateNewPlay(gameUpdater);
		}
	}



}
