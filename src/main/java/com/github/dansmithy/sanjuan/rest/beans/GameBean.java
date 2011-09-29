package com.github.dansmithy.sanjuan.rest.beans;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.dao.GameDao;
import com.github.dansmithy.sanjuan.game.GameService;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GameState;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameResource;
import com.github.dansmithy.sanjuan.security.AuthenticatedSessionProvider;

@Named
public class GameBean implements GameResource {
	
	private AuthenticatedSessionProvider userProvider;
	private GameDao gameDao;
	private GameService gameService;
	
	@Inject
	public GameBean(AuthenticatedSessionProvider userProvider, GameDao gameDao, GameService gameService) {
		super();
		this.userProvider = userProvider;
		this.gameDao = gameDao;
		this.gameService = gameService;
	}

	@Override
	public Game createNewGame(String ownerName) {
		
		String loggedInUser = userProvider.getAuthenticatedUsername();
		//check is logged in
		
		Player owner = new Player(ownerName);
		Game game = new Game(owner);
		gameDao.createGame(game);
		return game;
	}

	@Override
	public Player joinGame(Integer gameId, String playerName) {
		
		Game game = gameDao.getGame(gameId);
		Player player = new Player(playerName);
		game.addPlayer(new Player(playerName));
		gameDao.saveGame(game);
		return player;
	}

	@Override
	public Game getGame(Integer gameId) {
		return gameDao.getGame(gameId);
	}

	@Override
	public Game changeGameState(Integer gameId, String stateName) {
		
		// must be game owner
		
		Game game = gameDao.getGame(gameId);
		GameState gameState = GameState.valueOf(stateName);
		
		if (game.getState().equals(gameState)) {
			return game;
		}
		
		if (!game.getState().equals(GameState.RECRUITING) || !gameState.equals(GameState.PLAYING)) {
			throw new IllegalStateException(String.format("Can't change state from %s to %s.", game.getState(), gameState));
		}

		game = gameService.startGame(game);
		gameDao.saveGame(game);
		return game;
	}

	@Override
	public List<Game> getGames(String playerName) {
		if (playerName == null) {
			return gameDao.getGames();
		}
		return gameDao.getGamesForPlayer(playerName);
	}

	@Override
	public void deleteGame(Integer gameId) {
		gameDao.deleteGame(gameId);
	}


}
