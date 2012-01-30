package com.github.dansmithy.sanjuan.rest.beans;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.github.dansmithy.sanjuan.dao.GameDao;
import com.github.dansmithy.sanjuan.exception.AuthenticatedUserDoesNotMatchSubmittedData;
import com.github.dansmithy.sanjuan.exception.NotResourceOwnerAccessException;
import com.github.dansmithy.sanjuan.exception.RequestInvalidException;
import com.github.dansmithy.sanjuan.game.GameService;
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
import com.github.dansmithy.sanjuan.rest.jaxrs.GameResource;
import com.github.dansmithy.sanjuan.security.AuthenticatedSessionProvider;
import com.github.dansmithy.sanjuan.security.user.SanJuanRole;

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
	public Response getGame(Long gameId, Request request) {
		Game game = getGame(gameId);
		EntityTag tag = new EntityTag(Long.toString(game.getVersion()));
		ResponseBuilder builder = request.evaluatePreconditions(tag);
		if (builder != null) {
			return builder.build();
		} else {
			return Response.ok(game).tag(tag).build();
		}
	}	
	
	public Game getGame(Long gameId) {
		String loggedInUser = userProvider.getAuthenticatedUsername();
		Game game = gameService.getGame(gameId);
		
		if (!game.hasPlayer(loggedInUser)) {
			throw new NotResourceOwnerAccessException(String.format("You are not a player in this game."), NotResourceOwnerAccessException.NOT_YOUR_GAME);
		}
		return game;
	}
	
	@Override
	public Game getFullGame(Long gameId) {
		return getGame(gameId);
	}
	
	
	@Override
	public List<Game> getGames(String playerName, String stateName) {
		if (playerName == null && stateName == null) {
			return gameDao.getGames();
		}
		
		if (stateName == null) {
			
			String loggedInUser = userProvider.getAuthenticatedUsername();
			if (!playerName.equals(loggedInUser)) {
				throw new AuthenticatedUserDoesNotMatchSubmittedData(String.format("Unable to get games as authenticated user is not %s", playerName));
			}
			return gameService.getGamesForPlayer(playerName);
		} else {
			GameState gameState = GameState.valueOf(stateName);
			return gameService.getGamesInState(gameState);
		}
	}	

	@Override
	public Game createNewGame(String ownerName) {
		
		String loggedInUser = userProvider.getAuthenticatedUsername();
		if (!ownerName.equals(loggedInUser)) {
			throw new AuthenticatedUserDoesNotMatchSubmittedData(String.format("Unable to create game as authenticated user is not %s", ownerName));
		}
		
		return gameService.createNewGame(ownerName);
	}

	@Override
	public Player joinGame(Long gameId, String playerName) {

		String loggedInUser = userProvider.getAuthenticatedUsername();
		if (!playerName.equals(loggedInUser)) {
			throw new AuthenticatedUserDoesNotMatchSubmittedData(String.format("Unable to join game as authenticated user is not %s", playerName));
		}

		return gameService.addPlayerToGame(gameId, playerName);

	}
	
	@Override
	public void quitDuringRecruitment(Long gameId, String playerName) {

		String loggedInUser = userProvider.getAuthenticatedUsername();
		if (!playerName.equals(loggedInUser)) {
			throw new AuthenticatedUserDoesNotMatchSubmittedData(String.format("Unable to quit game as authenticated user is not %s", playerName));
		}

		gameService.removePlayerFromGame(gameId, playerName);
	}	

	@Override
	public Game changeGameState(Long gameId, String stateName) {
		
		GameState gameState = GameState.valueOf(stateName);
		
		if (GameState.PLAYING.equals(gameState)) {
			return startPlaying(gameId);
		} else if (GameState.ABANDONED.equals(gameState)) {
			return abandonGame(gameId);
		} else {
			throw new RequestInvalidException(String.format("Cannot change game to %s state", gameState));
		}
		
	}

	private Game abandonGame(Long gameId) {
		return gameService.abandonGame(gameId);
	}

	private Game startPlaying(Long gameId) {
		return gameService.startGame(gameId);
	}

	@Override
	public Game chooseRole(Long gameId, Integer roundNumber,
			Integer phaseNumber, RoleChoice choice) {
		
		PlayCoords coords = new PlayCoords(gameId, roundNumber, phaseNumber, 0);
		return gameService.selectRole(coords, choice);
	}

	@Override
	public Game makePlay(Long gameId, Integer roundNumber,
			Integer phaseNumber, Integer playNumber, PlayChoice playChoice) {
		
		PlayCoords coords = new PlayCoords(gameId, roundNumber, phaseNumber, playNumber);
		return gameService.makePlay(coords, playChoice);

	}
	
	@Override
	public Game makeGovernorPlay(Long gameId, Integer roundNumber, GovernorChoice governorChoice) {
		
		PlayCoords coords = new PlayCoords(gameId, roundNumber, 1, 0);
		return gameService.governorDiscard(coords, governorChoice);
	}	
	
	@Override
	public void deleteGame(Long gameId) {
		gameService.deleteGame(gameId, SanJuanRole.isAdminUser(userProvider.getUserDetails()));
	}

	@Override
	public Deck orderDeck(Long gameId, List<Integer> deckOrder) {
		// TODO verify game state
		return gameService.updateDeckOrder(gameId, deckOrder);
	}

	@Override
	public Play getPlay(Long gameId, Integer roundNumber, Integer phaseNumber,
			Integer playNumber) {
		return gameService.getPlay(gameId, roundNumber, phaseNumber, playNumber);
	}

	@Override
	public List<Tariff> orderTariffs(Long gameId, List<Integer> tariffOrder) {
		// TODO verify game state		
		return gameService.updateTariff(gameId, tariffOrder);
	}
}
