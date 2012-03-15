package com.github.dansmithy.sanjuan.game.roles;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayOffered;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

@Named
public class ProspectorProcessor implements RoleProcessor {

	@Override
	public Role getRole() {
		return Role.PROSPECTOR;
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		// do nothing

        Play play = gameUpdater.getNewPlay();
        Deck deck = gameUpdater.getGame().getDeck();
        Player player = gameUpdater.getNewPlayer();

        boolean withPrivilege = play.isHasPrivilige();

        List<Integer> prospectedCards = new ArrayList<Integer>();
        PlayOffered offered = play.createOffered();
        if (gameUpdater.getNewPhase().getLeadPlayer().equals(player.getName())) {

            for (int count = 0; count < player.getPlayerNumbers().getTotalProspectedCards(withPrivilege); count++) {
                Integer prospectedCard = deck.takeOne();
                prospectedCards.add(prospectedCard);
            }

            gameUpdater.updateDeck(deck);
        }
        offered.setProspected(prospectedCards);
        gameUpdater.updateDeck(deck);
    }

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {

        PlayOffered offered = gameUpdater.getCurrentPlay().getOffered();
		Player player = gameUpdater.getCurrentPlayer();
        player.addToHand(offered.getProspected());
		gameUpdater.updatePlayer(player);

	}

}
