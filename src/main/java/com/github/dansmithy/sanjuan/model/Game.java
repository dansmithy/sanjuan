package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;

import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.model.builder.TariffBuilder;

public class Game {
	
	private static final int STARTING_CARDS = 5;
	
	private Long gameId;
	private String owner;
	private GameState state = GameState.RECRUITING;
	private List<Player> players = new ArrayList<Player>();
	private Deck deck;
	private int currentTariff = 0;
	private List<Tariff> tariffs;
	private List<Round> rounds = new ArrayList<Round>();
	private Long version;
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
			throw new IllegalStateException("Not enough players to start a game");
		}
		state = GameState.PLAYING;
		List<Integer> orderedDeck = cardFactory.createOrderedDeck();
		for (Player player : players) {
			player.addToBuildings(orderedDeck.remove(orderedDeck.size()-1));
		}
		Collections.shuffle(orderedDeck);
		deck = new Deck(orderedDeck);
		for (Player player : players) {
			for (int count = 0; count < STARTING_CARDS; count++) {
				player.addToHand(deck.takeOne());
			}
		}
		tariffs = tariffBuilder.createRandomTariff();
		startNewRound(0);
	}

	private void startNewRound(int governorPlayerIndex) {
		rounds.add(new Round(players.get(governorPlayerIndex).getName()));
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
	
	@JsonIgnore
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
		for (Player player : players) {
			if (player.getName().equals(playerName)) {
				return true;
			}
		}
		return false;
	}
}
	
