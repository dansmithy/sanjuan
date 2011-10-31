package com.github.dansmithy.sanjuan.dao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.github.dansmithy.sanjuan.dao.util.MongoHelper;
import com.github.dansmithy.sanjuan.exception.ResourceNotFoundException;
import com.github.dansmithy.sanjuan.game.aspect.ProcessGame;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.GameState;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

@Named
public class MongoGameDao implements GameDao {

	private static final String GAME_ID_TYPE = "sanJuanGame";
	
	private static final String[] EMPTY_ARRAY = new String[0];
	private static final String[] BASIC_FIELDS = {  };
	private static final String[] BASIC_GAME_FIELDS = { "state", "gameId", "players", "owner" };

	
	private final MongoTemplate mongoTemplate;
	private final MongoIdGenerator mongoIdGenerator;

	@Inject
	public MongoGameDao(MongoTemplate mongoTemplate, MongoIdGenerator mongoIdGenerator) {
		super();
		this.mongoTemplate = mongoTemplate;
		this.mongoIdGenerator = mongoIdGenerator;
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.dao.GameDao#createGame(com.github.dansmithy.sanjuan.model.Game)
	 */
	@Override
	public Game createGame(Game game) {
		game.setGameId(mongoIdGenerator.getNextLongId(GAME_ID_TYPE));
		game.setVersion(1L);
		mongoTemplate.insert(game);
		return game;
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.dao.GameDao#getGame(java.lang.Long)
	 */
	@Override
	@ProcessGame
	public Game getGame(Long gameId) {
		return getGame(gameId, EMPTY_ARRAY, EMPTY_ARRAY);
	}
	
	private Game getGame(Long gameId, String[] includes, String[] excludes) {
		Query query = MongoHelper.createSimpleQuery("gameId", gameId);
		for (String include : includes) {
			query.fields().include(include);
		}
		for (String exclude : excludes) {
			query.fields().exclude(exclude);
		}
		Game game = mongoTemplate.findOne(query, Game.class);
		return convertNullTo404(game, "game", gameId.toString());
	}	

	private <T> T convertNullTo404(T response, String type, String id) {
		if (response != null) {
			return response;
		} else {
			throw new ResourceNotFoundException(String.format("Unable to locate %s with id %s.", type, id));
		}
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.dao.GameDao#saveGame(com.github.dansmithy.sanjuan.model.Game)
	 */
	@Override
	public void saveGame(Game game) {
		game.setVersion(game.getVersion()+1);
		mongoTemplate.save(game);
		
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.dao.GameDao#getGamesForPlayer(java.lang.String)
	 */
	@Override
	public List<Game> getGamesForPlayer(String playerName) {
		Query query = MongoHelper.createSimpleQuery("players.name", playerName);		
		for (String include : BASIC_GAME_FIELDS) {
			query.fields().include(include);
		}
		return mongoTemplate.find(query, Game.class);
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.dao.GameDao#getGamesInState(com.github.dansmithy.sanjuan.model.GameState)
	 */
	@Override
	public List<Game> getGamesInState(GameState state) {
		Query query = MongoHelper.createSimpleQuery("state", state.toString());		
		for (String include : BASIC_GAME_FIELDS) {
			query.fields().include(include);
		}
		return mongoTemplate.find(query, Game.class);
	}	

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.dao.GameDao#getGames()
	 */
	@Override
	public List<Game> getGames() {
		Query query = new Query();		
		for (String include : BASIC_GAME_FIELDS) {
			query.fields().include(include);
		}
		return mongoTemplate.find(query, Game.class);
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.dao.GameDao#deleteGame(java.lang.Long)
	 */
	@Override
	public void deleteGame(Long gameId) {
		Query query = MongoHelper.createSimpleQuery("gameId", gameId);
		mongoTemplate.remove(query, Game.class);
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.dao.GameDao#gameUpdate(java.lang.Long, com.github.dansmithy.sanjuan.model.update.GameUpdater)
	 */
	@Override
	@ProcessGame
	public Game gameUpdate(Long gameId, GameUpdater gameUpdater) {
		Update update = gameUpdater.createMongoUpdate();
		update.inc("version", 1);
		mongoTemplate.updateFirst(MongoHelper.createSimpleQuery("gameId", gameId), update, Game.class);
		return getGame(gameId);
	}


}
