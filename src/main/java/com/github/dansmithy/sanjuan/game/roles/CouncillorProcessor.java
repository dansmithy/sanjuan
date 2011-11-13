package com.github.dansmithy.sanjuan.game.roles;

import java.util.List;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.exception.PlayChoiceInvalidException;
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
		Deck deck = gameUpdater.getGame().getDeck();
		PlayerNumbers numbers = gameUpdater.getNewPlayer().getPlayerNumbers();
		
		boolean withPrivilege = play.isHasPrivilige();
		int numberOfCardsToChooseFrom = numbers.getTotalCouncillorOfferedCards(withPrivilege);
		
		List<Integer> cardsOnOffer = deck.take(numberOfCardsToChooseFrom);
		
		PlayOffered offered = play.createOffered();
		offered.setCouncilOffered(cardsOnOffer);
		offered.setCouncilRetainCount(numbers.getCouncillorRetainCards());
		offered.setCouncilCanDiscardHandCards(numbers.isCouncillorCanDiscardHandCards());
		
		gameUpdater.updateDeck(deck);				
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {
		
		Play play = gameUpdater.getCurrentPlay();
		Game game = gameUpdater.getGame();
		Deck deck = game.getDeck();
		Player player = gameUpdater.getCurrentPlayer();
		PlayerNumbers numbers = player.getPlayerNumbers();
		boolean withPrivilege = play.isHasPrivilige();
		
		deck.discard(playChoice.getCouncilDiscarded());
		gameUpdater.updateDeck(game.getDeck());
		
		int numberCardsMustDiscard = numbers.getTotalCouncillorOfferedCards(withPrivilege) - numbers.getCouncillorRetainCards();
		int numberCardsDiscarded = playChoice.getCouncilDiscarded().size();
		if (numberCardsDiscarded < numberCardsMustDiscard) {
			throw new PlayChoiceInvalidException(String.format("Chosen to discard %d cards, but must discard %d.", numberCardsDiscarded, numberCardsMustDiscard), PlayChoiceInvalidException.UNDER_DISCARD);
		}
		if (numberCardsDiscarded > numberCardsMustDiscard) {
			throw new PlayChoiceInvalidException(String.format("Chosen to discard %d cards, but must discard only %d.", numberCardsDiscarded, numberCardsMustDiscard), PlayChoiceInvalidException.OVER_DISCARD);
		}
		
		List<Integer> offered = play.getOffered().getCouncilOffered();
		for (Integer discardedCard : playChoice.getCouncilDiscarded()) {
			if (offered.contains(discardedCard)) {
				offered.remove(discardedCard);
			} else if (player.getHand().contains(discardedCard)) {
				if (!gameUpdater.getCurrentPlayer().getPlayerNumbers().isCouncillorCanDiscardHandCards()) {
					throw new PlayChoiceInvalidException(String.format("Card %d is not a possible choice to discard", discardedCard), PlayChoiceInvalidException.NOT_OWNED_COUNCIL_DISCARD);
				}
				player.getHand().remove(discardedCard);
				deck.discard(discardedCard);
			} else {
				throw new PlayChoiceInvalidException(String.format("Card %d is not a possible choice to discard", discardedCard), PlayChoiceInvalidException.NOT_OWNED_COUNCIL_DISCARD);
			}
		}
		player.addToHand(offered);
		
		gameUpdater.updatePlayer(player);
		gameUpdater.completedPlay(play, playChoice);
		gameUpdater.createNextStep();
		
		if (!gameUpdater.isPhaseChanged()) {
			initiateNewPlay(gameUpdater);
		}
	}

}
