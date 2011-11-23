package com.github.dansmithy.sanjuan.model.update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Update;

import com.github.dansmithy.sanjuan.exception.SanJuanUnexpectedException;
import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GovernorPhase;
import com.github.dansmithy.sanjuan.model.Phase;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Round;
import com.github.dansmithy.sanjuan.model.Tariff;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayCoords;

public class GameUpdater {
	
	private Map<String, PartialUpdate> updates = new HashMap<String, PartialUpdate>();

	private final PlayCoords playCoords;
	private PlayCoords nextPlayCoords;
	private final Game game;
	
	public GameUpdater(Game game) {
		this(game, PlayCoords.createFromGame(game));
	}
	
	public GameUpdater(Game game, PlayCoords playCoords) {
		super();
		this.game = game;
		this.playCoords = playCoords;
	}
	
	public boolean matchesCoords(PlayCoords otherPlayCoords) {
		return otherPlayCoords.equals(playCoords);
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
	
	public void updateTariffs(List<Tariff> tariffs) {
		updates.put("tariffs", new PartialUpdate("tariffs", tariffs));
	}	
	
	public void completedPlay(Play play, PlayChoice playChoice) {
		play.completePlay(playChoice);
		updates.put("play", new PartialUpdate(playCoords.getPlayLocation(), play));
	}
	
	public void updatePhase(Phase phase) {
		updates.put("phase", new PartialUpdate(playCoords.getPhaseLocation(), phase));
	}	
	
	public void updateCurrentTariff(int currentTariff) {
		updates.put("currentTariff", new PartialUpdate("currentTariff", currentTariff));
	}	
	
	/**
	 * For completion only
	 */
	public void updateGameState() {
		updates.put("gamestate", new PartialUpdate("state", game.getState()));
	}	
	
	/**
	 * For completion only
	 */
	public void updatePlayers() {
		updates.remove("player");
		updates.put("players", new PartialUpdate("players", game.getPlayers()));
	}
	
	/**
	 * For completion only
	 */
	public void updateWinner() {
		updates.put("winner", new PartialUpdate("winner", game.getWinner()));
	}
	
	public Round nextRound(PlayerCycle cycle) {
		String nextGovernor = cycle.next(game.getCurrentRound().getGovernor());
		int playerCount = game.getPlayers().size(); 
		GovernorPhase governorPhase = game.createGovernorPhase(nextGovernor, cycle);
		Round nextRound = new Round(nextGovernor, playerCount, governorPhase);
		game.addRound(nextRound);
		return nextRound;
	}
	
	public void createNextStep() {
		PlayerCycle cycle = game.createPlayerCycle();
		Round currentRound = game.getCurrentRound();
		if (currentRound.isComplete()) {
			Round round = nextRound(cycle);
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
		return getPlayer(getCurrentUsername());
	}
	
	private String getCurrentUsername() {
		if (playCoords.getPlayNumber() == 0) {
			return getCurrentPhase().getLeadPlayer();
		} else {
			return getCurrentPlay().getPlayer();
		}
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
		return getCurrentRound().getPhases().get(playCoords.getPhaseIndex());
	}
	
	public Round getCurrentRound() {
		return game.getRounds().get(playCoords.getRoundIndex());
	}

	public boolean isPhaseChanged() {
		return nextPlayCoords != null && nextPlayCoords.getPlayNumber() == 0;
	}
	
	public boolean isCreatingFirstPlay() {
		return nextPlayCoords.getPlayNumber() == 1;
	}
	
	public Phase getNewPhase() {
		return game.getRounds().get(nextPlayCoords.getRoundIndex()).getPhases().get(nextPlayCoords.getPhaseIndex());
	}
	
	public Play getNewPlay() {
		return getNewPhase().getPlays().get(nextPlayCoords.getPlayIndex());
	}

}
