package com.github.dansmithy.sanjuan.game.roles;

import java.util.ArrayList;
import java.util.List;

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
public class CouncillorProcessor implements RoleProcessor {

	@Override
	public Role getRole() {
		return Role.COUNCILLOR;
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		
		Play play = gameUpdater.getNewPlay();
		boolean withPrivilege = play.isHasPrivilige();
		PlayerNumbers numbers = gameUpdater.getNewPlayer().getPlayerNumbers();
		int numberOfCardsToChooseFrom = numbers.getTotalCouncillorOfferedCards(withPrivilege);
		Deck deck = gameUpdater.getGame().getDeck();
		PlayOffered offered = new PlayOffered();
		offered.setCouncilOffered(deck.take(numberOfCardsToChooseFrom));
		offered.setCouncilRetainCount(numbers.getTotalCouncillorRetainCards(withPrivilege));
		offered.setCouncilCanDiscardHandCards(numbers.isCouncillorCanDiscardHandCards());
		gameUpdater.updateDeck(deck);			
		play.setOffered(offered);		
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {
		Play play = gameUpdater.getCurrentPlay();
		Game game = gameUpdater.getGame();
		Deck deck = game.getDeck();
		deck.discard(playChoice.getCouncilDiscardedAsArray());
		gameUpdater.updateDeck(game.getDeck());
		
		String playerName = play.getPlayer();
		Player player = game.getPlayer(playerName);

		List<Integer> offered = new ArrayList<Integer>(play.getOffered().getCouncilOffered());
		for (Integer discardedCard : playChoice.getCouncilDiscarded()) {
			if (offered.contains(discardedCard)) {
				offered.remove(discardedCard);
			} else if (player.getHand().contains(discardedCard)) {
				// TODO verify can discard hand cards
				player.getHand().remove(discardedCard);
				deck.discard(discardedCard);
			} else {
				throw new SanJuanUnexpectedException(String.format("Card %d is not a possible choice to discard", discardedCard));
			}
		}
		player.addToHand(offered);
		
		gameUpdater.updatePlayer(player);
		
		play.makePlay(playChoice);		
		gameUpdater.completedPlay(play);
		gameUpdater.createNextStep();
		
		if (!gameUpdater.isPhaseChanged()) {
			initiateNewPlay(gameUpdater);
		}
	}

}
