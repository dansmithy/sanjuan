package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;

import com.github.dansmithy.sanjuan.exception.IllegalGameStateException;
import com.github.dansmithy.sanjuan.exception.SanJuanUnexpectedException;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.model.builder.TariffBuilder;
import com.github.dansmithy.sanjuan.model.update.PlayerCycle;

public class Game {
	
	private static final int STARTING_CARDS = 5;
	private static final int FINAL_BUILDING_COUNT = 12;
	
	private Long gameId;
	private String owner;
	private GameState state = GameState.RECRUITING;
	private List<Player> players = new ArrayList<Player>();
	private Deck deck;
	private int currentTariff = 0;
	private List<Tariff> tariffs;
	private List<Round> rounds = new ArrayList<Round>();
	private Long version;
	private String winner;
	@Id
	private ObjectId id;
	
	public Game(Player owner) {
		this.owner = owner.getName();
		players.add(owner);
	}
	
	public Game() {
		
	}
	
	public void startPlaying(CardFactory cardFactory, TariffBuilder tariffBuilder) {
		if (players.size() < 2) {
			throw new IllegalGameStateException("Not enough players to start a game.");
		}
		state = GameState.PLAYING;
		initiateDeck(cardFactory);
		
		for (Player player : players) {
			player.addToHand(deck.take(STARTING_CARDS));
		}
		tariffs = tariffBuilder.createRandomTariff();
		startNewRound(0);
	}

	private void initiateDeck(CardFactory cardFactory) {
		boolean useRandomOrder = deck == null;
		if (useRandomOrder) {
			List<Integer> orderedDeck = cardFactory.createOrderedDeck();
			deck = new Deck(orderedDeck);
		}
		for (Player player : players) {
			player.moveToBuildings(deck.takeOne());
		}
		if (useRandomOrder) {
			deck.reshuffleDeck();
		}
	}

	private void startNewRound(int governorPlayerIndex) {
		rounds.add(new Round(players.get(governorPlayerIndex).getName(), players.size()));
	}

	public Long getGameId() {
		return gameId;
	}
	
	public int getRoundNumber() {
		return rounds.size();
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public GameState getState() {
		return state;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Deck getDeck() {
		return deck;
	}

	public int getCurrentTariff() {
		return currentTariff;
	}

	public void setCurrentTariff(int currentTariff) {
		this.currentTariff = currentTariff;
	}

	@JsonIgnore
	public List<Tariff> getTariffs() {
		return tariffs;
	}

	public List<Round> getRounds() {
		return rounds;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public Long getVersion() {
		return version;
	}
	
	public void setVersion(Long version) {
		this.version = version;
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public boolean hasPlayer(String playerName) {
		return getPlayerIndex(playerName) != -1;
	}

	public int getPlayerIndex(String playerName) {
		int index = 0;
		for (Player player : players) {
			if (player.getName().equals(playerName)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@JsonIgnore
	public Round getCurrentRound() {
		return rounds.isEmpty() ? null : rounds.get(getRoundNumber()-1);
	}

	public PlayerCycle createPlayerCycle() {
		List<String> playerNames = new ArrayList<String>();
		for (Player player : players) {
			playerNames.add(player.getName());
		}
		return new PlayerCycle(playerNames);
	}

	public Round nextRound(PlayerCycle cycle) {
		String nextGovernor = cycle.next(getCurrentRound().getGovernor());
		Round nextRound = new Round(nextGovernor, players.size());
		rounds.add(nextRound);
		return nextRound;
	}

	public Player getPlayer(String playerName) {
		for (Player player : players) {
			if (playerName.equals(player.getName())) {
				return player;
			}
		}
		throw new SanJuanUnexpectedException(String.format("No player with name %s in game [%d].", playerName, gameId));
	}

	public boolean hasReachedEndCondition() {
		for (Player player : players) {
			if (player.getBuildings().size() == FINAL_BUILDING_COUNT) {
				return true;
			}
		}
		return false;
	}

	public void markCompleted() {
		state = GameState.COMPLETED;
	}

	public boolean isComplete() {
		return state.equals(GameState.COMPLETED);
	}

	public String getWinner() {
		return winner;
	}

	public void calculateWinner() {
		List<Player> winningPlayers = new ArrayList<Player>();
		winningPlayers.add(players.get(0));
		for (int count = 1; count < players.size(); count++) {
			Player player = players.get(count);
			if (player.getVictoryPoints() > winningPlayers.get(0).getVictoryPoints()) {
				winningPlayers.clear();
				winningPlayers.add(0, player);
			} else if (player.getVictoryPoints() == winningPlayers.get(0).getVictoryPoints()) {
				if (player.getHand().size() > winningPlayers.get(0).getHand().size()) {
					winningPlayers.clear();
					winningPlayers.add(0, player);
				} else if (player.getHand().size() == winningPlayers.get(0).getHand().size()) {
					winningPlayers.add(player);
				}
			}
		}
		winner = "";
		String delimiter = "";
		for (Player winningPlayer : winningPlayers) {
			winner += delimiter + winningPlayer.getName();
			delimiter = ", ";
		}
	}

}
	
