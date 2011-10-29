package com.github.dansmithy.sanjuan.game;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.dao.GameDao;
import com.github.dansmithy.sanjuan.exception.IllegalGameStateException;
import com.github.dansmithy.sanjuan.exception.NotResourceOwnerAccessException;
import com.github.dansmithy.sanjuan.game.aspect.ProcessGame;
import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GameState;
import com.github.dansmithy.sanjuan.model.Phase;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.model.builder.TariffBuilder;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayCoords;
import com.github.dansmithy.sanjuan.model.input.RoleChoice;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;
import com.github.dansmithy.sanjuan.security.AuthenticatedSessionProvider;

@Named
public class DatastoreGameService implements GameService {

	private TariffBuilder tariffBuilder;
	private CardFactory cardFactory;
	private final GameDao gameDao;
	private final AuthenticatedSessionProvider userProvider;
	private final RoleProcessorProvider roleProcessorProvider;
	private final CalculationService calculationService;
	
	@Inject
	public DatastoreGameService(GameDao gameDao, RoleProcessorProvider roleProcessorProvider, AuthenticatedSessionProvider userProvider, CalculationService calculationService, TariffBuilder tariffBuilder, CardFactory cardFactory) {
		super();
		this.gameDao = gameDao;
		this.roleProcessorProvider = roleProcessorProvider;
		this.userProvider = userProvider;
		this.calculationService = calculationService;
		this.tariffBuilder = tariffBuilder;
		this.cardFactory = cardFactory;
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#doDanny(java.lang.Long)
	 */
	@Override
	public Game getGame(Long gameId) {
		return gameDao.getGame(gameId);
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#getGamesForPlayer(java.lang.String)
	 */
	@Override
	public List<Game> getGamesForPlayer(String playerName) {
		// TODO Incomplete only?
		return gameDao.getGamesForPlayer(playerName);
	}	
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#getGamesInState(com.github.dansmithy.sanjuan.model.GameState)
	 */
	@Override
	public List<Game> getGamesInState(GameState gameState) {
		return gameDao.getGamesInState(gameState);
	}	

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#createNewGame(java.lang.String)
	 */
	@Override
	@ProcessGame
	public Game createNewGame(String ownerName) {
		Player owner = new Player(ownerName);
		Game game = new Game(owner);
		gameDao.createGame(game);
		return game;
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#addPlayerToGame(java.lang.Long, java.lang.String)
	 */
	@Override
	public Player addPlayerToGame(Long gameId, String playerName) {
		Game game = getGame(gameId);

		// Note: ideally need to use versions here so that it doesn't test and
		// pass hasplayer/state, have another thread then also pass and add the
		// player/change state, then try to update in this thread. Version
		// numbers should do that ok!
		
		if (!game.getState().equals(GameState.RECRUITING)) {
			throw new IllegalGameStateException(String.format("Cannot join a game when in a %s state.", game.getState().toString()));
		}
		if (game.hasPlayer(playerName)) {
			throw new IllegalGameStateException(String.format("%s is already a player for this game.", playerName));
		}
		Player player = new Player(playerName);
		game.addPlayer(player);
		gameDao.saveGame(game);
		return player;
	}

	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#startGame(java.lang.Long)
	 */
	@Override
	@ProcessGame
	public Game startGame(Long gameId) {
		Game game = gameDao.getGame(gameId);
		
		String loggedInUser = userProvider.getAuthenticatedUsername();
		if (!loggedInUser.equals(game.getOwner())) {
			throw new NotResourceOwnerAccessException(String.format("Must be game owner to start game."));
		}
		
		if (game.getState().equals(GameState.PLAYING)) {
			return game; // already in desired state
		}
		
		if (!game.getState().equals(GameState.RECRUITING)) {
			throw new IllegalStateException(String.format("Can't change state from %s to %s.", game.getState(), GameState.PLAYING));
		}

		game.startPlaying(cardFactory, tariffBuilder);
		gameDao.saveGame(game);
		return game;
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#selectRole(com.github.dansmithy.sanjuan.model.input.PlayCoords, com.github.dansmithy.sanjuan.model.input.RoleChoice)
	 */
	@Override
	public Game selectRole(PlayCoords playCoords, RoleChoice choice) {
		
		// TODO verify play coords IS current phase
		Game game = getGame(playCoords.getGameId());
		String loggedInUser = userProvider.getAuthenticatedUsername();
		GameUpdater gameUpdater = new GameUpdater(game, loggedInUser);
		Phase phase = gameUpdater.getCurrentPhase();
		if (!loggedInUser.equals(phase.getLeadPlayer())) {
			throw new NotResourceOwnerAccessException(String.format("It is not your turn to choose role."));
		}

		Role role = choice.getRole();
		phase.selectRole(role);
		gameUpdater.updatePhase(phase);
		gameUpdater.createNextStep();
		calculationService.processPlayer(gameUpdater.getNewPlayer());
		RoleProcessor roleProcessor = roleProcessorProvider.getProcessor(role);
		roleProcessor.initiateNewPlay(gameUpdater);
		
		return gameDao.gameUpdate(game.getGameId(), gameUpdater);
	}	
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#makePlay(com.github.dansmithy.sanjuan.model.input.PlayCoords, com.github.dansmithy.sanjuan.model.input.PlayChoice)
	 */
	@Override
	public Game makePlay(PlayCoords coords, PlayChoice playChoice) {
		
		Game game = getGame(coords.getGameId());
		
		// TODO verify coords is current
		// TODO verify play is current one
		
		GameUpdater gameUpdater = new GameUpdater(game, userProvider.getAuthenticatedUsername());
		calculationService.processPlayer(gameUpdater.getCurrentPlayer());
		Role role = game.getCurrentRound().getCurrentPhase().getRole();
		RoleProcessor roleProcessor = roleProcessorProvider.getProcessor(role);
		if (playChoice.getSkip() != null && playChoice.getSkip()) {
			playSkip(gameUpdater, playChoice);
			if (!gameUpdater.isPhaseChanged()) {
				roleProcessor.initiateNewPlay(gameUpdater);
			}
		} else {
			roleProcessor.makeChoice(gameUpdater, playChoice);
		}
		// do winner
		if (game.isComplete()) {
			calculationService.processPlayers(game.getPlayers());
			gameUpdater.updatePlayers();
			game.calculateWinner();
			gameUpdater.updateWinner();
		}		
		return gameDao.gameUpdate(game.getGameId(), gameUpdater);
	}		
	
	private void playSkip(GameUpdater gameUpdater, PlayChoice playChoice) {
		
		Play play = gameUpdater.getCurrentPlay();
		gameUpdater.completedPlay(play, playChoice);
		gameUpdater.createNextStep();
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#deleteGame(java.lang.Long)
	 */
	@Override
	public void deleteGame(Long gameId) {
		Game game = gameDao.getGame(gameId);
		
		// TODO restrict to recruiting games.
		
		// TODO introduce "abandoned" state for games: any player can abandon a game and is marked as such.
		
		String loggedInUser = userProvider.getAuthenticatedUsername();
		if (!loggedInUser.equals(game.getOwner())) {
			throw new NotResourceOwnerAccessException(String.format("Must be game owner to delete game."));
		}
		
		gameDao.deleteGame(gameId);
	}

	@Override
	public Deck updateDeckOrder(Long gameId, List<Integer> deckOrder) {
		Game game = gameDao.getGame(gameId);	
		GameUpdater gameUpdater = new GameUpdater(game, userProvider.getAuthenticatedUsername());
		Deck deck = new Deck(deckOrder);
		gameUpdater.updateDeck(deck);
		return gameDao.gameUpdate(gameId, gameUpdater).getDeck();
	}


}
