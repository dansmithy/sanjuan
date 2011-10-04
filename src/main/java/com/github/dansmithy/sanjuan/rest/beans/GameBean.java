package com.github.dansmithy.sanjuan.rest.beans;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.github.dansmithy.sanjuan.dao.GameDao;
import com.github.dansmithy.sanjuan.exception.NotResourceOwnerAccessException;
import com.github.dansmithy.sanjuan.exception.SanJuanUnexpectedException;
import com.github.dansmithy.sanjuan.game.GameService;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GameState;
import com.github.dansmithy.sanjuan.model.Phase;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.RoleChoice;
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
		if (!ownerName.equals(loggedInUser)) {
			throw new NotResourceOwnerAccessException(String.format("Unable to create game as authenticated user is not %s", ownerName));
		}
		
		Player owner = new Player(ownerName);
		Game game = new Game(owner);
		gameDao.createGame(game);
		return game;
	}

	@Override
	public Player joinGame(Integer gameId, String playerName) {

		String loggedInUser = userProvider.getAuthenticatedUsername();
		if (!playerName.equals(loggedInUser)) {
			throw new NotResourceOwnerAccessException(String.format("Unable to join game as authenticated user is not %s", playerName));
		}

		Game game = gameDao.getGame(gameId);
		
		if (game.hasPlayer(playerName)) {
			throw new WebApplicationException(Status.NO_CONTENT);
		}
		
		// TODO check game state
		
		Player player = new Player(playerName);
		game.addPlayer(new Player(playerName));
		gameDao.saveGame(game);
		return player;
	}

	private Game getGame(Integer gameId) {
		return gameDao.getGame(gameId);
	}
	
	@Override
	public Response getGame(Integer gameId, Request request) {
		
		Game game = getGame(gameId);
		EntityTag tag = new EntityTag(Long.toString(game.getVersion()));
		ResponseBuilder builder = request.evaluatePreconditions(tag);
		if (builder != null) {
			return builder.build();
		} else {
			return Response.ok(game).tag(tag).build();
		}
	}

	@Override
	public Game changeGameState(Integer gameId, String stateName) {
		
		Game game = gameDao.getGame(gameId);
		
		String loggedInUser = userProvider.getAuthenticatedUsername();
		if (!loggedInUser.equals(game.getOwner())) {
			throw new NotResourceOwnerAccessException(String.format("Must be game owner to start game."));
		}
		
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
	public List<Game> getGames(String playerName, String state) {
		if (playerName == null && state == null) {
			return gameDao.getGames();
		}
		
		if (state == null) {
			
			String loggedInUser = userProvider.getAuthenticatedUsername();
			if (!playerName.equals(loggedInUser)) {
				throw new NotResourceOwnerAccessException(String.format("Unable to get games as authenticated user is not %s", playerName));
			}
			
			return gameDao.getGamesForPlayer(playerName);
		} else {
			
			return gameDao.getGamesInState(state);
		}
	}

	@Override
	public void deleteGame(Integer gameId) {
		
		Game game = gameDao.getGame(gameId);
		
		String loggedInUser = userProvider.getAuthenticatedUsername();
		if (!loggedInUser.equals(game.getOwner())) {
			throw new NotResourceOwnerAccessException(String.format("Must be game owner to delete game."));
		}
		
		gameDao.deleteGame(gameId);
	}

	@Override
	public Game chooseRole(Integer gameId, Integer roundIndex,
			Integer phaseIndex, RoleChoice choice) {
		
		Game game = getGame(gameId);
		Phase phase = game.getRounds().get(roundIndex-1).getPhases().get(phaseIndex-1);

		String loggedInUser = userProvider.getAuthenticatedUsername();
		if (!loggedInUser.equals(phase.getLeadPlayer())) {
			throw new NotResourceOwnerAccessException(String.format("It is not your turn to choose role."));
		}

		phase.selectRole(choice.getRole());
		gameDao.updatePhase(gameId, roundIndex-1, phaseIndex-1, phase);
		return game;
	}

	@Override
	public Game makePlay(Integer gameId, Integer roundIndex,
			Integer phaseIndex, Integer playIndex, PlayChoice playChoice) {
		
		if (playChoice.getBuild() != null) {
			return playBuild(gameId, roundIndex, phaseIndex, playIndex, playChoice);
		} else {
			return null;
		}
	}
	
	private Player getCurrentPlayer(Game game) {
		for (Player player : game.getPlayers()) {
			if (userProvider.getAuthenticatedUsername().equals(player.getName())) {
				return player;
			}
		}
		throw new SanJuanUnexpectedException(String.format("Current user %s not one of the players in this game", userProvider.getAuthenticatedUsername()));
	}

	private Game playBuild(Integer gameId, Integer roundIndex,
			Integer phaseIndex, Integer playIndex, PlayChoice playChoice) {
		
		// verify access
		
		Game game = getGame(gameId);
		long existingVersion = game.getVersion();
		game.setVersion(existingVersion+1);
		Phase phase = game.getRounds().get(roundIndex-1).getPhases().get(phaseIndex-1);
		Play play = phase.getPlays().get(playIndex-1);
		play.makePlay(playChoice);
		game.moveToNext();
		Player player = getCurrentPlayer(game);
		int playerIndex = game.getPlayerIndex(player.getName());
		player.moveToBuildings(playChoice.getBuild());
		player.removeHandCards(playChoice.getPayment());
		game.getDeck().discard(playChoice.getPayment());
		gameDao.updatePlayer(gameId, playerIndex, player);
		gameDao.updatePlay(gameId, roundIndex-1, phaseIndex-1, playIndex-1, play);
		gameDao.updateDeck(gameId, game.getDeck());
		gameDao.updateVersion(gameId, game.getVersion());
		
		return game;
	}


}
