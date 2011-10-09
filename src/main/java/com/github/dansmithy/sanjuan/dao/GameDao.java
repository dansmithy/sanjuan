package com.github.dansmithy.sanjuan.dao;

import java.util.List;

import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GameState;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

public interface GameDao {

	Game createGame(Game game);

	Game getGame(Long gameId);

	void saveGame(Game game);

	List<Game> getGamesForPlayer(String playerName);

	List<Game> getGamesInState(GameState state);

	List<Game> getGames();

	void deleteGame(Long gameId);

	Game gameUpdate(Long gameId, GameUpdater gameUpdater);

}