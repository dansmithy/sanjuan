package com.github.dansmithy.spring;

import static org.hamcrest.Matchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.junit.Assert;

import com.github.dansmithy.driver.DefaultValues;
import com.github.dansmithy.driver.GameDriverSession;
import com.github.dansmithy.driver.RequestValues;
import com.github.dansmithy.driver.TranslatedValues;
import com.github.dansmithy.json.JsonHashTranslator;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.User;
import com.github.dansmithy.sanjuan.model.input.GovernorChoice;
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
	public Response getGetGamesFor(String username) {
		RequestValues urlValues = createRequest(String.format("username : %s", username));
		return new SpringResponse(gameResource.getGames(urlValues.get("username"), null));
	}
	
	@Override
	public Response getGamesInState(String state) {
		return new SpringResponse(gameResource.getGames(null, state));
	}

	@Override
	public Response getAllGames() {
		return new SpringResponse(gameResource.getGames(null, null));
	}	
	
	protected RequestValues createTranslatedUserRequest(String username,
			String password) {
		return getTranslatedValues().aliasRequestValues(new RequestValues().add("username", username).add("password", password));
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
		return new SpringResponse(gameResource.changeGameState(gameId, "PLAYING"));
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
	public Response makesGovernorPlay(String urlData, String postJson) {
		RequestValues urlValues = createRequest(urlData);
		JSON json = new JsonHashTranslator(translatedValues).translate(JSONSerializer.toJSON(postJson));
		GovernorChoice choice = (GovernorChoice)JSONObject.toBean((JSONObject)json, GovernorChoice.class);
		return new SpringResponse(gameResource.makeGovernorPlay(gameId, Integer.valueOf(urlValues.get("round")), choice));
	}
	
	@Override
	public Response getGame() {
		return new SpringResponse(gameResource.getGame(gameId));
	}
	
	@Override
	public Response getGame(long gameId) {
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
		return gameId == null ? null : gameId.toString();
	}
	
	@Override
	public void setGameId(String gameId) {
		this.gameId = Long.valueOf(gameId);
	}

	@Override
	public Response deleteGame(String gameId) {
		gameResource.deleteGame(Long.valueOf(gameId));
		return new SpringResponse(null);
	}
	

	@Override
	public Response quitGame(String username) {
		Assert.assertThat("No gameId set so cannot quit game.", gameId, is(not(nullValue())));
		gameResource.quitDuringRecruitment(Long.valueOf(gameId), translatedValues.get(username));
		return new SpringResponse(null);
	}
	
	@Override
	public Response abandonGame() {
		return new SpringResponse(gameResource.changeGameState(gameId, "ABANDONED"));
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
		return translatedValues.aliasRequestValues(new RequestValues().addAll(defaults).addReadableData(data));
	}

	@Override
	public Response getUser() {
		// TODO Auto-generated method stub
		return null;
	}


}
