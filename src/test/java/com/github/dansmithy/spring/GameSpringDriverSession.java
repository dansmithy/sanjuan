package com.github.dansmithy.spring;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.github.dansmithy.driver.DefaultValues;
import com.github.dansmithy.driver.GameDriverSession;
import com.github.dansmithy.driver.RequestValues;
import com.github.dansmithy.driver.TranslatedValues;
import com.github.dansmithy.json.JsonHashTranslator;
import com.github.dansmithy.sanjuan.exception.NotResourceOwnerAccessException;
import com.github.dansmithy.sanjuan.exception.ResourceNotFoundException;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.User;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.RoleChoice;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameResource;
import com.github.dansmithy.sanjuan.rest.jaxrs.UserResource;
import com.github.restdriver.serverdriver.http.response.Response;

public class GameSpringDriverSession implements GameDriverSession {

	private final TranslatedValues translatedValues;
	private GameResource gameResource;
	private UserResource userResource;
	 
	private Long gameId;
	
	public GameSpringDriverSession(TranslatedValues translatedValues,
			GameResource gameResource, UserResource userResource) {
		super();
		this.translatedValues = translatedValues;
		this.gameResource = gameResource;
		this.userResource = userResource;
	}

	@Override
	public void createUser(String username) {
		RequestValues requestValues = createTranslatedUserRequest(username, DefaultValues.PASSWORD);
		User user = new User();
		user.setPassword(requestValues.get("password"));
		user.setUsername(requestValues.get("username"));
		userResource.createUser(user);
	}
	
	@Override
	public Response updateUser(String username, String postJson) {
		RequestValues urlValues = createRequest(String.format("username : %s", username));
		JSON json = new JsonHashTranslator(translatedValues).translate(JSONSerializer.toJSON(postJson));
		User user = (User)JSONObject.toBean((JSONObject)json, User.class);
		return new SpringResponse(userResource.updateUser(urlValues.get("username"), user));
	}	
	
	@Override
	public Response getUsers() {
		return new SpringResponse(userResource.getUsers());
	}
	
	@Override
	public Response getGetGamesFor(String username) {
		RequestValues urlValues = createRequest(String.format("username : %s", username));
		return new SpringResponse(gameResource.getGames(urlValues.get("username"), null));
	}
	
	protected RequestValues createTranslatedUserRequest(String username,
			String password) {
		return getTranslatedValues().translateRequestValues(new RequestValues().add("username", username).add("password", password));
	}	

	@Override
	public void deleteUser(String data) {
		RequestValues requestValues = createRequest(DefaultValues.USER, data);
		userResource.deleteUser(requestValues.get("username"));
	}

	@Override
	public void orderDeck(String gameId, List<Integer> order) {
		gameResource.orderDeck(Long.valueOf(gameId), order);
	}
	
	@Override
	public void orderTariff(String gameId, List<Integer> order) {
		gameResource.orderTariffs(Long.valueOf(gameId), order);
	}	

	@Override
	public Response createGame(String data) {
		RequestValues requestValues = createRequest(data);
		Game game = gameResource.createNewGame(requestValues.get("username"));
		this.gameId = game.getGameId();
		return new SpringResponse(game);
	}

	@Override
	public Response joinGame(String gameId, String data) {
		this.gameId = Long.valueOf(gameId);
		RequestValues requestValues = createRequest(data);
		return new SpringResponse(gameResource.joinGame(this.gameId, requestValues.get("username")));
	}

	@Override
	public Response startGame() {
		return new SpringResponse(gameResource.startGame(gameId, "PLAYING"));
	}

	@Override
	public Response chooseRole(String urlData, String postData) {
		RequestValues urlValues = createRequest(urlData);
		RequestValues postValues = createRequest(postData);
		RoleChoice choice = new RoleChoice();
		choice.setRole(Role.valueOf(postValues.get("role")));
		return new SpringResponse(gameResource.chooseRole(gameId, Integer.valueOf(urlValues.get("round")), Integer.valueOf(urlValues.get("phase")), choice));
	}

	@Override
	public Response makePlayChoice(String urlData, String postJson) {
		RequestValues urlValues = createRequest(urlData);
		JSON json = new JsonHashTranslator(translatedValues).translate(JSONSerializer.toJSON(postJson));
		PlayChoice choice = (PlayChoice)JSONObject.toBean((JSONObject)json, PlayChoice.class);
		return new SpringResponse(gameResource.makePlay(gameId, Integer.valueOf(urlValues.get("round")), Integer.valueOf(urlValues.get("phase")), Integer.valueOf(urlValues.get("play")), choice));
	}

	@Override
	public Response getGame() {
		return new SpringResponse(gameResource.getGame(gameId));
	}

	@Override
	public TranslatedValues getTranslatedValues() {
		return translatedValues;
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getGameId() {
		return gameId.toString();
	}

	@Override
	public void deleteAnyGame() {
		if (gameId != null) {
			try {
				gameResource.deleteGame(Long.valueOf(gameId));
			} catch (ResourceNotFoundException e) {
				// do nothing! only for spring implementaiton
			} catch (NotResourceOwnerAccessException e) {
				// do nothing! only for spring implementaiton
			}
			
		}		
	}

	@Override
	public void addTranslatedValues(String data) {
		RequestValues values = new RequestValues().addReadableData(data);
		for (Map.Entry<String, String> entry : values) {
			getTranslatedValues().add(entry.getKey(), entry.getValue());
		}
	}

	private RequestValues createRequest(String data) {
		return createRequest(emptyMap(), data);
	}	
	
	private Map<String, String> emptyMap() {
		return Collections.emptyMap();
	}
	private RequestValues createRequest(Map<String, String> defaults, String data) {
		return translatedValues.translateRequestValues(new RequestValues().addAll(defaults).addReadableData(data));
	}

}
