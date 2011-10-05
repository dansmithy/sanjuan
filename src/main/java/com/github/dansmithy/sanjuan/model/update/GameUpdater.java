package com.github.dansmithy.sanjuan.model.update;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.document.mongodb.query.Update;

import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Phase;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Round;
import com.github.dansmithy.sanjuan.model.input.PlayCoords;

public class GameUpdater {
	
	private Map<String, PartialUpdate> updates = new HashMap<String, PartialUpdate>();

	private final PlayCoords playCoords;
	
	public GameUpdater(PlayCoords playCoords) {
		super();
		this.playCoords = playCoords;
	}

	public void updatePlayer(int playerIndex, Player player) {
		updates.put("player", new PartialUpdate(String.format("players.%d", playerIndex), player));
	}
	
	public void updateDeck(Deck deck) {
		updates.put("deck", new PartialUpdate("deck", deck));
	}
	
	public void completedPlay(Play play) {
		updates.put("play", new PartialUpdate(String.format("rounds.%d.phases.%d.plays.%d", playCoords.getRoundIndex(), playCoords.getPhaseIndex(), playCoords.getPlayIndex()), play));
	}
	
	public void updatePhase(Phase phase) {
		updates.put("phase", new PartialUpdate(String.format("rounds.%d.phases.%d", playCoords.getRoundIndex(), playCoords.getPhaseIndex()), phase));
	}	
	
	public void createNextStep(Game game) {
		PlayerCycle cycle = game.createPlayerCycle();
		Round currentRound = game.getCurrentRound();
		if (currentRound.isComplete()) {
			Round round = game.nextRound(cycle);
			updates.put("newRound", new PartialUpdate(String.format("rounds.%d", playCoords.getRoundIndex()+1), round));
		} else {
			Phase currentPhase = currentRound.getCurrentPhase();
			if (currentPhase.isComplete()) {
				Phase phase = currentRound.nextPhase(cycle);
				updates.put("newPhase", new PartialUpdate(String.format("rounds.%d.phases.%d", playCoords.getRoundIndex(), playCoords.getPhaseIndex()+1), phase));
			} else {
				Play play = currentPhase.nextPlay(cycle);
				updates.put("newPlay", new PartialUpdate(String.format("rounds.%d.phases.%d.plays.%d", playCoords.getRoundIndex(), playCoords.getPhaseIndex(), playCoords.getPlayIndex()+1), play));
			}
		}
	}
	
	public Update createMongoUpdate() {
		Update update = new Update();
		for (PartialUpdate partial : updates.values()) {
			update.set(partial.getUpdatePath(), partial.getUpdateObject());
		}
		return update;
	}

	public Play getCurrentPlay(Game game) {
		return game.getRounds().get(playCoords.getRoundIndex()).getPhases().get(playCoords.getPhaseIndex()).getPlays().get(playCoords.getPlayIndex());
	}

}
