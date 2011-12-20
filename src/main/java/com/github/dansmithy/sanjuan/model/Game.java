package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.annotation.Id;

import com.github.dansmithy.sanjuan.exception.IllegalGameStateException;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.model.builder.TariffBuilder;
import com.github.dansmithy.sanjuan.model.update.PlayerCycle;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
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
			throw new IllegalGameStateException("Not enough players to start a game.", IllegalGameStateException.NOT_ENOUGH_PLAYERS);
		}
		
		initiateDeck(cardFactory);
		initiateTariffs(tariffBuilder);
		
		for (Player player : players) {
			player.addToHand(deck.take(STARTING_CARDS));
		}
		
		state = GameState.PLAYING;
		startNewRound(0);
	}

	private void initiateTariffs(TariffBuilder tariffBuilder) {
		boolean useRandomOrder = tariffs == null;
		if (useRandomOrder) {
			tariffs = tariffBuilder.createRandomTariff();
		}
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

	@JsonIgnore
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

	public Game addRound(Round round) {
		rounds.add(round);
		return this;
	}
	
	public Player getPlayer(final String playerName) {
		return Iterables.find(players, new Predicate<Player>() {

			@Override
			public boolean apply(Player player) {
				return player.getName().equals(playerName);
			}
		});
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
				if (player.getHandCards().size() > winningPlayers.get(0).getHandCards().size()) {
					winningPlayers.clear();
					winningPlayers.add(0, player);
				} else if (player.getHandCards().size() == winningPlayers.get(0).getHandCards().size()) {
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

	public GovernorPhase createGovernorPhase(String nextGovernor, PlayerCycle cycle) {
		List<GovernorStep> governorSteps = new ArrayList<GovernorStep>();
		for (String playerName : cycle.startAt(nextGovernor)) {
			addGovernorStepIfRequired(getPlayer(playerName), governorSteps);
		}
		return new GovernorPhase(governorSteps);
	}

	private void addGovernorStepIfRequired(Player player,
			List<GovernorStep> governorSteps) {
		int cardsToDiscard = player.getHandCards().size() - player.getPlayerNumbers().getCardsCanHold();
		if (cardsToDiscard < 0) {
			cardsToDiscard = 0;
		}
		boolean chapelOwner = player.getPlayerNumbers().isChapelOwner();
		if (cardsToDiscard > 0 || chapelOwner) {
			governorSteps.add(new GovernorStep(player.getName(), cardsToDiscard, chapelOwner));
		}
	}
	
}
	
