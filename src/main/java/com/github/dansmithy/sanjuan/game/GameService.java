package com.github.dansmithy.sanjuan.game;

import java.util.List;

import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GameState;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Tariff;
import com.github.dansmithy.sanjuan.model.input.GovernorChoice;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayCoords;
import com.github.dansmithy.sanjuan.model.input.RoleChoice;

public interface GameService {

	Game getGame(Long gameId);

	List<Game> getGamesForPlayer(String playerName);

	List<Game> getGamesInState(GameState gameState);

	Game createNewGame(String ownerName);

	Player addPlayerToGame(Long gameId, String playerName);

	Game startGame(Long gameId);

	Game selectRole(PlayCoords playCoords, RoleChoice choice);

	Game makePlay(PlayCoords coords, PlayChoice playChoice);

	Game governorDiscard(PlayCoords playCoords, GovernorChoice governorChoice);
	
	void deleteGame(Long gameId);

	Deck updateDeckOrder(Long gameId, List<Integer> deckOrder);

	Play getPlay(Long gameId, Integer roundNumber, Integer phaseNumber,
			Integer playNumber);

	List<Tariff> updateTariff(Long gameId, List<Integer> tariffOrder);


}