package com.github.dansmithy.sanjuan.dao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.query.Query;
import org.springframework.data.document.mongodb.query.Update;

import com.github.dansmithy.sanjuan.dao.util.MongoHelper;
import com.github.dansmithy.sanjuan.exception.ResourceNotFoundException;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Phase;

@Named
public class GameDao {

	private static final String GAME_ID_TYPE = "sanJuanGame";
	
	private static final String[] EMPTY_ARRAY = new String[0];
	private static final String[] BASIC_FIELDS = {  };
	private static final String[] BASIC_GAME_FIELDS = { "state", "gameId", "players", "owner" };

	
	private final MongoTemplate mongoTemplate;
	private final MongoIdGenerator mongoIdGenerator;

	@Inject
	public GameDao(MongoTemplate mongoTemplate, MongoIdGenerator mongoIdGenerator) {
		super();
		this.mongoTemplate = mongoTemplate;
		this.mongoIdGenerator = mongoIdGenerator;
	}
	
	public Game createGame(Game game) {
		game.setGameId(mongoIdGenerator.getNextLongId(GAME_ID_TYPE));
		game.setVersion(1L);
		mongoTemplate.insert(game);
		return game;
	}

	public Game getGame(Integer gameId) {
		return getGame(gameId, EMPTY_ARRAY, EMPTY_ARRAY);
	}
	
	public Game getGame(Integer gameId, String[] includes, String[] excludes) {
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
	
	public void saveGame(Game game) {
		game.setVersion(game.getVersion()+1);
		mongoTemplate.save(game);
		
	}

	public List<Game> getGamesForPlayer(String playerName) {
		Query query = MongoHelper.createSimpleQuery("players.name", playerName);		
		for (String include : BASIC_GAME_FIELDS) {
			query.fields().include(include);
		}
		return mongoTemplate.find(query, Game.class);
	}
	
	public List<Game> getGamesInState(String state) {
		Query query = MongoHelper.createSimpleQuery("state", state);		
		for (String include : BASIC_GAME_FIELDS) {
			query.fields().include(include);
		}
		return mongoTemplate.find(query, Game.class);
	}	

	public List<Game> getGames() {
		Query query = new Query();		
		for (String include : BASIC_GAME_FIELDS) {
			query.fields().include(include);
		}
		return mongoTemplate.find(query, Game.class);
	}

	public void deleteGame(Integer gameId) {
		Query query = MongoHelper.createSimpleQuery("gameId", gameId);
		mongoTemplate.remove(query, Game.class);
	}

	public void updatePhase(Integer gameId, Integer roundIndex,
			Integer phaseIndex, Phase phase) {
		String updateElement = String.format("rounds.%d.phases.%d", roundIndex, phaseIndex);
		Update update = new Update().set(updateElement, phase);
		mongoTemplate.updateFirst(MongoHelper.createSimpleQuery("gameId", gameId), update, Game.class);
		
	}


}
