package com.github.dansmithy.sanjuan.game;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.dao.GameDao;
import com.github.dansmithy.sanjuan.exception.IllegalGameStateException;
import com.github.dansmithy.sanjuan.exception.AccessUnauthorizedRuntimeException;
import com.github.dansmithy.sanjuan.exception.PlayChoiceInvalidException;
import com.github.dansmithy.sanjuan.game.aspect.ProcessGame;
import com.github.dansmithy.sanjuan.model.Deck;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GameState;
import com.github.dansmithy.sanjuan.model.GovernorStep;
import com.github.dansmithy.sanjuan.model.Phase;
import com.github.dansmithy.sanjuan.model.PhaseState;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.PlayState;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.RoundState;
import com.github.dansmithy.sanjuan.model.Tariff;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.model.builder.TariffBuilder;
import com.github.dansmithy.sanjuan.model.input.GovernorChoice;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayCoords;
import com.github.dansmithy.sanjuan.model.input.RoleChoice;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;
import com.github.dansmithy.sanjuan.security.AuthenticatedSessionProvider;
import com.github.dansmithy.sanjuan.util.CollectionUtils;

@Named
public class DatastoreGameService implements GameService {

	private static final int MAXIMUM_PLAYER_COUNT = 4;
	
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
			throw new IllegalGameStateException(String.format("Cannot join a game when in a %s state.", game.getState().toString()), IllegalGameStateException.NOT_RECRUITING);
		}
		if (game.getPlayers().size() == MAXIMUM_PLAYER_COUNT) {
			throw new IllegalGameStateException(String.format("This game already has the maximum number of players (%d)", MAXIMUM_PLAYER_COUNT), IllegalGameStateException.TOO_MANY_PLAYERS);
		}		
		if (game.hasPlayer(playerName)) {
			throw new IllegalGameStateException(String.format("%s is already a player for this game.", playerName), IllegalGameStateException.ALREADY_PLAYER);
		}
		Player player = new Player(playerName);
		game.addPlayer(player);
		gameDao.saveGame(game);
		return player;
	}


	@Override
	public void removePlayerFromGame(Long gameId, String playerName) {
		
		Game game = getGame(gameId);
		
		if (!game.getState().equals(GameState.RECRUITING)) {
			throw new IllegalGameStateException(String.format("Cannot leave a game when in a %s state.", game.getState().toString()), IllegalGameStateException.NOT_RECRUITING);
		}
		
		if (playerName.equals(game.getOwner())) {
			throw new IllegalGameStateException(String.format("Game owner cannot quit game, but can delete it.", game.getState().toString()), IllegalGameStateException.OWNER_CANNOT_QUIT);
		}
		
		game.getPlayers().remove(game.getPlayerIndex(playerName));
		gameDao.saveGame(game);
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
			throw new AccessUnauthorizedRuntimeException(String.format("Must be game owner to start game."), AccessUnauthorizedRuntimeException.NOT_CORRECT_USER);
		}
		
		if (game.getState().equals(GameState.PLAYING)) {
			return game; // already in desired state
		}
		
		if (!game.getState().equals(GameState.RECRUITING)) {
			throw new IllegalGameStateException(String.format("Can't change state from %s to %s.", game.getState(), GameState.PLAYING), IllegalGameStateException.NOT_RECRUITING);
		}

		game.startPlaying(cardFactory, tariffBuilder);
		gameDao.saveGame(game);
		return game;
	}
	
	@Override
	public Game abandonGame(Long gameId) {
		Game game = gameDao.getGame(gameId);
		
		String loggedInUser = userProvider.getAuthenticatedUsername();
		
		if (!game.hasPlayer(loggedInUser)) {
			throw new AccessUnauthorizedRuntimeException(String.format("%s is not a player in this game.", loggedInUser), AccessUnauthorizedRuntimeException.NOT_YOUR_GAME);
		}
		
		if (!game.getState().equals(GameState.PLAYING)) {
			throw new IllegalGameStateException(String.format("Can't change state from %s to %s.", game.getState(), GameState.ABANDONED), IllegalGameStateException.NOT_ACTIVE_STATE);
		}

		GameUpdater gameUpdater = new GameUpdater(game);
		game.markAbandoned(loggedInUser);
		gameUpdater.updateGameState();
		gameUpdater.updateAbandonedBy();
		return gameDao.gameUpdate(gameId, gameUpdater);		
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#selectRole(com.github.dansmithy.sanjuan.model.input.PlayCoords, com.github.dansmithy.sanjuan.model.input.RoleChoice)
	 */
	@Override
	public Game selectRole(PlayCoords playCoords, RoleChoice choice) {
		
		Game game = getGame(playCoords.getGameId());
		String loggedInUser = userProvider.getAuthenticatedUsername();
		
		if (!game.hasPlayer(loggedInUser)) {
			throw new AccessUnauthorizedRuntimeException(String.format("%s is not a player in this game.", loggedInUser), AccessUnauthorizedRuntimeException.NOT_YOUR_GAME);
		}
		
		if (!game.getState().equals(GameState.PLAYING)) {
			throw new IllegalGameStateException(String.format("Game not active, so cannot choose a role now."), IllegalGameStateException.NOT_PLAYING);
		}
		
		GameUpdater gameUpdater = new GameUpdater(game);
		
		if (!gameUpdater.matchesCoords(playCoords)) {
			throw new IllegalGameStateException(String.format("Cannot modify round %d, phase %d as not the current phase.", playCoords.getRoundNumber(), playCoords.getPhaseNumber()), IllegalGameStateException.PHASE_NOT_ACTIVE);
		}

		if (gameUpdater.getCurrentRound().getState().equals(RoundState.GOVERNOR)) {
			throw new IllegalGameStateException(String.format("Cannot modify round %d, phase %d as still in governor phase.", playCoords.getRoundNumber(), playCoords.getPhaseNumber()), IllegalGameStateException.PHASE_NOT_ACTIVE);
		}

		Phase phase = gameUpdater.getCurrentPhase();
		if (!phase.getState().equals(PhaseState.AWAITING_ROLE_CHOICE)) {
			throw new IllegalGameStateException(String.format("Cannot choose role at this point in the game."), IllegalGameStateException.NOT_AWAITING_ROLE_CHOICE);
		}
		
		if (!loggedInUser.equals(phase.getLeadPlayer())) {
			throw new AccessUnauthorizedRuntimeException(String.format("It is not your turn to choose role."), AccessUnauthorizedRuntimeException.NOT_CORRECT_USER);
		}
		

		Role role = choice.getRole();
		if (role.equals(Role.GOVERNOR)) {
			throw new IllegalGameStateException(String.format("Cannot choose the Governor role."), PlayChoiceInvalidException.INVALID_ROLE);
		}
		
		if (!gameUpdater.getCurrentRound().getRemainingRoles().contains(role)) {
			throw new IllegalGameStateException(String.format("Cannot choose role at this point in the game."), IllegalGameStateException.ROLE_ALREADY_TAKEN);
		}
		
		phase.selectRole(role);
		gameUpdater.updatePhase(phase);
		gameUpdater.createNextStep();
		RoleProcessor roleProcessor = roleProcessorProvider.getProcessor(role);
		roleProcessor.initiateNewPlay(gameUpdater);
		
		return gameDao.gameUpdate(game.getGameId(), gameUpdater);
	}	
	
	@Override
	public Game governorDiscard(PlayCoords playCoords, GovernorChoice governorChoice) {
		
		Game game = getGame(playCoords.getGameId());
		GameUpdater gameUpdater = new GameUpdater(game);
		String loggedInUser = userProvider.getAuthenticatedUsername();
		
		if (!game.hasPlayer(loggedInUser)) {
			throw new AccessUnauthorizedRuntimeException(String.format("%s is not a player in this game.", loggedInUser), AccessUnauthorizedRuntimeException.NOT_YOUR_GAME);
		}
		
		if (!game.getState().equals(GameState.PLAYING)) {
			throw new IllegalGameStateException(String.format("Game not active, so cannot play now."), IllegalGameStateException.NOT_PLAYING);
		}
		
		if (!gameUpdater.matchesCoords(playCoords)) {
			throw new IllegalGameStateException(String.format("Cannot modify round %d", playCoords.getRoundNumber()), IllegalGameStateException.PHASE_NOT_ACTIVE);
		}		
		
		if (!gameUpdater.getCurrentRound().getState().equals(RoundState.GOVERNOR)) {
			throw new IllegalGameStateException(String.format("Game not active, so cannot play now."), IllegalGameStateException.PHASE_NOT_ACTIVE);
		}

		if (!gameUpdater.getCurrentRound().getState().equals(RoundState.GOVERNOR)) {
			throw new IllegalGameStateException(String.format("Game not active, so cannot play now."), IllegalGameStateException.PHASE_NOT_ACTIVE);
		}

		GovernorStep step = gameUpdater.getCurrentRound().getGovernorPhase().getCurrentStep();
		// cannot be null, cos wouldn't be GOVERNOR state (verified by previous check)
		
 		if (!loggedInUser.equals(step.getPlayerName())) {
			throw new AccessUnauthorizedRuntimeException(String.format("Not your turn to make Governor phase choices."), AccessUnauthorizedRuntimeException.NOT_CORRECT_USER);
 		}

		if (CollectionUtils.hasDuplicates(governorChoice.getCardsToDiscard())) {
			throw new PlayChoiceInvalidException(String.format("Discarded list of cards contains duplicates, not allowed."), PlayChoiceInvalidException.DUPLICATE_CHOICE);
		}
		
		Player player = game.getPlayer(loggedInUser);

		int cardsShouldDiscardCount = player.getHandCards().size() - player.getPlayerNumbers().getCardsCanHold();
 		int cardsRequestedToDiscardCount = governorChoice.getCardsToDiscard().size();
 		
 		if (cardsRequestedToDiscardCount > cardsShouldDiscardCount) {
 			throw new PlayChoiceInvalidException(String.format("Chosen to discard %d cards, but only need to discard %d", cardsRequestedToDiscardCount, cardsShouldDiscardCount), PlayChoiceInvalidException.OVER_DISCARD);
 		}

 		if (cardsRequestedToDiscardCount < cardsShouldDiscardCount) {
 			throw new PlayChoiceInvalidException(String.format("Chosen to discard %d cards, but need to discard %d", cardsRequestedToDiscardCount, cardsShouldDiscardCount), PlayChoiceInvalidException.UNDER_DISCARD);
 		}
 		
 		if (!player.getHandCards().containsAll(governorChoice.getCardsToDiscard())) {
 			throw new PlayChoiceInvalidException(String.format("Cannot discard card as not one of your hand cards"), PlayChoiceInvalidException.NOT_OWNED_HAND_CARD);
 		}
		step.setCardsToDiscard(governorChoice.getCardsToDiscard());
		step.setState(PlayState.COMPLETED);
		
		player.removeHandCards(governorChoice.getCardsToDiscard());
		game.getDeck().discard(governorChoice.getCardsToDiscard());
		
		gameUpdater.updateDeck(game.getDeck());
		gameUpdater.updatePlayer(player);
		gameUpdater.updateGovernorStep(step);
		
		return gameDao.gameUpdate(game.getGameId(), gameUpdater);
		
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#makePlay(com.github.dansmithy.sanjuan.model.input.PlayCoords, com.github.dansmithy.sanjuan.model.input.PlayChoice)
	 */
	@Override
	public Game makePlay(PlayCoords coords, PlayChoice playChoice) {
		
		Game game = getGame(coords.getGameId());
		String loggedInUser = userProvider.getAuthenticatedUsername();
		GameUpdater gameUpdater = new GameUpdater(game);
		
		if (!game.hasPlayer(loggedInUser)) {
			throw new AccessUnauthorizedRuntimeException(String.format("%s is not a player in this game.", loggedInUser), AccessUnauthorizedRuntimeException.NOT_YOUR_GAME);
		}
		
		if (!game.getState().equals(GameState.PLAYING)) {
			throw new IllegalGameStateException(String.format("Game not active, so cannot play now."), IllegalGameStateException.NOT_PLAYING);
		}
		
		if (!gameUpdater.matchesCoords(coords)) {
			throw new IllegalGameStateException(String.format("Cannot modify round %d, phase %d, play %d as not the current play.", coords.getRoundNumber(), coords.getPhaseNumber(), coords.getPlayNumber()), IllegalGameStateException.PLAY_NOT_ACTIVE);
		}
		
		if (!gameUpdater.getCurrentPlay().getState().equals(PlayState.AWAITING_INPUT)) {
			throw new IllegalGameStateException(String.format("Cannot make play at this point in the game."), IllegalGameStateException.PLAY_NOT_ACTIVE);
		}		
		
		if (!loggedInUser.equals(gameUpdater.getCurrentPlayer().getName())) {
			throw new AccessUnauthorizedRuntimeException("It is not your turn to play.", AccessUnauthorizedRuntimeException.NOT_CORRECT_USER);
		}
		
		Role role = game.getCurrentRound().getCurrentPhase().getRole();
		RoleProcessor roleProcessor = roleProcessorProvider.getProcessor(role);
		roleProcessor.makeChoice(gameUpdater, playChoice);
		
		calculationService.processPlayers(game.getPlayers());
		
		if (gameHasJustEnded(game, gameUpdater)) {
			handleGameCompletion(game, gameUpdater);
		} else {
			gameUpdater.createNextStep();
			if (!gameUpdater.isPhaseChanged()) {
				roleProcessor.initiateNewPlay(gameUpdater);
			}
		}
		
		return gameDao.gameUpdate(game.getGameId(), gameUpdater);
	}
	
	private boolean gameHasJustEnded(Game game, GameUpdater gameUpdater) {
		return game.hasReachedEndCondition() && gameUpdater.getCurrentPhase().isComplete();
	}

	private void handleGameCompletion(Game game, GameUpdater gameUpdater) {
		game.markCompleted();
		game.calculateWinner();
		game.setEnded(new Date());
		
		gameUpdater.updateWinner();			
		gameUpdater.updatePlayers();
		gameUpdater.updateEndedDate();
		gameUpdater.updateGameState();
	}		
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.game.GameService#deleteGame(java.lang.Long)
	 */
	@Override
	public void deleteGame(Long gameId, boolean isAdminUser) {
		Game game = gameDao.getGame(gameId);
		
		if (!isAdminUser) {
			String loggedInUser = userProvider.getAuthenticatedUsername();
			
			if (loggedInUser.equals(game.getOwner())) {
				if (!GameState.RECRUITING.equals(game.getState())) {
					throw new IllegalGameStateException(String.format("Game must be still recruiting in order to delete."), IllegalGameStateException.NOT_RECRUITING);
				}
			} else {
				throw new AccessUnauthorizedRuntimeException(String.format("Must be game owner to delete game."), AccessUnauthorizedRuntimeException.NOT_CORRECT_USER);
			}
		}
		
		gameDao.deleteGame(gameId);
	}

	@Override
	public Deck updateDeckOrder(Long gameId, List<Integer> deckOrder) {
		Game game = gameDao.getGame(gameId);	
		GameUpdater gameUpdater = new GameUpdater(game);
		Deck deck = new Deck(deckOrder);
		gameUpdater.updateDeck(deck);
		return gameDao.gameUpdate(gameId, gameUpdater).getDeck();
	}

	@Override
	public Play getPlay(Long gameId, Integer roundNumber, Integer phaseNumber,
			Integer playNumber) {
		Game game = gameDao.getGame(gameId);
		GameUpdater gameUpdater = new GameUpdater(game, new PlayCoords(gameId, roundNumber, phaseNumber, playNumber));
		return gameUpdater.getCurrentPlay();
	}

	@Override
	public List<Tariff> updateTariff(Long gameId, List<Integer> tariffOrder) {
		Game game = gameDao.getGame(gameId);	
		GameUpdater gameUpdater = new GameUpdater(game);
		List<Tariff> tariffs = tariffBuilder.createTariff(tariffOrder);
		gameUpdater.updateTariffs(tariffs);
		return gameDao.gameUpdate(gameId, gameUpdater).getTariffs();
	}


}