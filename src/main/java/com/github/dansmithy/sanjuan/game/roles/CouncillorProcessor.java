package com.github.dansmithy.sanjuan.game.roles;

import javax.inject.Named;

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
		gameUpdater.updateDeck(deck);			
		play.setOffered(offered);		
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {
		Play play = gameUpdater.getCurrentPlay();
		Game game = gameUpdater.getGame();
		game.getDeck().discard(playChoice.getCouncilDiscardedAsArray());
		gameUpdater.updateDeck(game.getDeck());
		
		String playerName = play.getPlayer();
		Player player = game.getPlayer(playerName);

		for (Integer offeredCard : play.getOffered().getCouncilOffered()) {
			if (!playChoice.getCouncilDiscarded().contains(offeredCard)) {
				player.addToHand(offeredCard);
			}
		}
		gameUpdater.updatePlayer(player);
		
		play.makePlay(playChoice);		
		gameUpdater.completedPlay(play);
		gameUpdater.createNextStep();
		
		if (!gameUpdater.isPhaseChanged()) {
			initiateNewPlay(gameUpdater);
		}
	}

}
