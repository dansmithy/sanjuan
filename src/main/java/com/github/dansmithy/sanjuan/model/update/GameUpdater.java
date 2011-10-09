package com.github.dansmithy.sanjuan.model.update;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.document.mongodb.query.Update;

import com.github.dansmithy.sanjuan.exception.SanJuanUnexpectedException;
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
	private PlayCoords nextPlayCoords;
	private final Game game;
	private final String currentUser;
	
	public GameUpdater(Game game, String currentUser) {
		this.game = game;
		this.currentUser = currentUser;
		this.playCoords = PlayCoords.createFromGame(game);
	}
	
	public Game getGame() {
		return game;
	}

	public void updatePlayer(Player player) {
		int playerIndex = game.getPlayerIndex(player.getName());
		updates.put("player", new PartialUpdate(String.format("players.%d", playerIndex), player));
	}
	
	public void updateDeck(Deck deck) {
		updates.put("deck", new PartialUpdate("deck", deck));
	}
	
	public void completedPlay(Play play) {
		updates.put("play", new PartialUpdate(playCoords.getPlayLocation(), play));
	}
	
	public void updatePhase(Phase phase) {
		updates.put("phase", new PartialUpdate(playCoords.getPhaseLocation(), phase));
	}	
	
	public void createNextStep() {
		PlayerCycle cycle = game.createPlayerCycle();
		Round currentRound = game.getCurrentRound();
		if (currentRound.isComplete()) {
			Round round = game.nextRound(cycle);
			nextPlayCoords = playCoords.nextRound();		
			updates.put("newRound", new PartialUpdate(nextPlayCoords.getRoundLocation(), round));

		} else {
			Phase currentPhase = currentRound.getCurrentPhase();
			if (currentPhase.isComplete()) {
				Phase phase = currentRound.nextPhase(cycle);
				nextPlayCoords = playCoords.nextPhase();
				updates.put("newPhase", new PartialUpdate(nextPlayCoords.getPhaseLocation(), phase));
			} else {
				Play play = currentPhase.nextPlay(cycle);
				nextPlayCoords = playCoords.nextPlay();
				if (nextPlayCoords.getPlayNumber() != 1) {
					updates.put("newPlay", new PartialUpdate(nextPlayCoords.getPlayLocation(), play));
				}
			}
		}
	}
	
	public Player getCurrentPlayer() {
		return getPlayer(currentUser);
	}
	
	private Player getPlayer(String playerName) {
		for (Player player : game.getPlayers()) {
			if (playerName.equals(player.getName())) {
				return player;
			}
		}
		throw new SanJuanUnexpectedException(String.format("Current user %s not one of the players in this game", playerName));
		
	}
	
	public Player getNewPlayer() {
		return getPlayer(getNewPlay().getPlayer());
	}	
	
	public Update createMongoUpdate() {
		Update update = new Update();
		for (PartialUpdate partial : updates.values()) {
			update.set(partial.getUpdatePath(), partial.getUpdateObject());
		}
		return update;
	}

	public Play getCurrentPlay() {
		return getCurrentPhase().getPlays().get(playCoords.getPlayIndex());
	}
	
	public Phase getCurrentPhase() {
		return game.getRounds().get(playCoords.getRoundIndex()).getPhases().get(playCoords.getPhaseIndex());
	}

	public boolean isPhaseChanged() {
		return nextPlayCoords != null && nextPlayCoords.getPlayNumber() == 0;
	}
	
	public Play getNewPlay() {
		return game.getRounds().get(nextPlayCoords.getRoundIndex()).getPhases().get(nextPlayCoords.getPhaseIndex()).getPlays().get(nextPlayCoords.getPlayIndex());
	}

}
