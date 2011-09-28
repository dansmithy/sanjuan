package com.github.dansmithy.sanjuan.rest.beans;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.dao.GameDao;
import com.github.dansmithy.sanjuan.game.GameService;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GameState;
import com.github.dansmithy.sanjuan.model.Phase;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.input.RoleChoice;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameResource;
import com.github.dansmithy.sanjuan.security.UserProvider;

@Named
public class GameBean implements GameResource {
	
	private UserProvider userProvider;
	private GameDao gameDao;
	private GameService gameService;
	
	@Inject
	public GameBean(UserProvider userProvider, GameDao gameDao, GameService gameService) {
		super();
		this.userProvider = userProvider;
		this.gameDao = gameDao;
		this.gameService = gameService;
	}

	@Override
	public Game createNewGame(String ownerName) {
		
		String loggedInUser = userProvider.getLoggedInUser();
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

	@Override
	public Game chooseRole(Integer gameId, Integer roundIndex,
			Integer phaseIndex, RoleChoice choice) {
		
		Game game = getGame(gameId);
		Phase phase = game.getRounds().get(roundIndex-1).getPhases().get(phaseIndex-1);
		phase.selectRole(choice.getRole());
		gameDao.updatePhase(gameId, roundIndex-1, phaseIndex-1, phase);
		return game;
	}


}
