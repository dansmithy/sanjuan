package com.github.dansmithy.sanjuan.model.update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Update;

import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GovernorPhase;
import com.github.dansmithy.sanjuan.model.GovernorStep;
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
	private Map<String, Player> playerMap = new HashMap<String, Player>();
	
	public GameUpdater(Game game) {
		this(game, PlayCoords.createFromGame(game));
	}
	
	public GameUpdater(Game game, PlayCoords playCoords) {
		super();
		this.game = game;
		this.playCoords = playCoords;
		addPlayers(game.getPlayers());
	}
	
	private void addPlayers(List<Player> players) {
		for (Player player : players) {
			playerMap.put(player.getName(), player);
		}
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
	public void updateEndedDate() {
		updates.put("ended", new PartialUpdate("ended", game.getEnded()));
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
	
	public void updateAbandonedBy() {
		updates.put("abandonedBy", new PartialUpdate("abandonedBy", game.getAbandonedBy()));
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
				if (isNotFirstPlay()) { // will have been saved as part of the phase update, made as part of "Select role" if first play
					updates.put("newPlay", new PartialUpdate(nextPlayCoords.getPlayLocation(), play));
				}
			}
		}
	}
	
	public Player getCurrentPlayer() {
		return game.getPlayer(getCurrentUsername());
	}
	
	private String getCurrentUsername() {
		if (playCoords.getPlayNumber() == 0) {
			return getCurrentPhase().getLeadPlayer();
		} else {
			return getCurrentPlay().getPlayer();
		}
	}

	public Player getNewPlayer() {
		return game.getPlayer(getNewPlay().getPlayer());
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
		return nextPlayCoords != null && nextPlayCoords.hasChangedPhaseOrRound();
	}
	
	public boolean isCreatingFirstPlay() {
		return nextPlayCoords.getPlayNumber() == 1;
	}

    public Round getNewRound() {
        return game.getRounds().get(nextPlayCoords.getRoundIndex());
    }

    public Phase getNewPhase() {
        return getNewRound().getPhases().get(nextPlayCoords.getPhaseIndex());
    }

    public Play getNewPlay() {
        return getNewPhase().getPlays().get(nextPlayCoords.getPlayIndex());
    }
	
	public GovernorStep getGovernorStep(String player) {
		for (GovernorStep step : getCurrentRound().getGovernorPhase().getGovernorSteps()) {
			if (step.getPlayerName().equals(player)) {
				return step;
			}
		}
		return null;
	}
	
	public GovernorPhase getGovernorPhase() {
		return getCurrentRound().getGovernorPhase();
	}	
	
	private int getStepIndex(GovernorStep stepToMatch) {
		int index = 0;
		for (GovernorStep step : getCurrentRound().getGovernorPhase().getGovernorSteps()) {
			if (step.getPlayerName().equals(stepToMatch.getPlayerName())) {
				return index;
			}
			index++;
		}
		return -1;
	}	

	public void updateGovernorStep(GovernorStep step) {
		int governorStepIndex = getStepIndex(step);
		String stepLocation = String.format("%s.governorPhase.governorSteps.%d", playCoords.getRoundLocation(), governorStepIndex);
		updates.put("governorStep", new PartialUpdate(stepLocation, step));
	}

	private boolean isNotFirstPlay() {
		return nextPlayCoords.getPlayNumber() != 1;
	}
}
