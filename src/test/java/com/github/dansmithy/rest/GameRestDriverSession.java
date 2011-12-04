package com.github.dansmithy.rest;

import static com.github.restdriver.serverdriver.RestServerDriver.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.github.dansmithy.driver.DefaultValues;
import com.github.dansmithy.driver.GameDriverSession;
import com.github.dansmithy.driver.RequestValues;
import com.github.dansmithy.driver.TranslatedValues;
import com.github.dansmithy.exception.AcceptanceTestException;
import com.github.dansmithy.json.JsonHashTranslator;
import com.github.restdriver.serverdriver.http.AnyRequestModifier;
import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;

public class GameRestDriverSession implements GameDriverSession {

	private static final String JSON_CONTENT_TYPE = "application/json";
	private static final Header ACCEPT_JSON_HEADER = header("Accept", JSON_CONTENT_TYPE);

	private final TranslatedValues translatedValues;
	private final String sessionId;
	private final String wsBaseUri;
	
	private String gameId;
	
	public GameRestDriverSession(String baseUri, String sessionId) {
		this(baseUri, sessionId, new TranslatedValues());
	}
	
	public GameRestDriverSession(String baseUri, String sessionId, TranslatedValues translatedValues) {
		this.sessionId = sessionId;
		this.translatedValues = translatedValues;
		this.wsBaseUri = String.format("%s/ws", baseUri);
	}

	//** USER METHODS ** //
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#createUser(java.lang.String)
	 */
	@Override
	public void createUser(String username) {
		RequestValues requestValues = createTranslatedUserRequest(username, DefaultValues.PASSWORD);
		Response response = post(wsBaseUri + "/users", body(requestValues.toJson(), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
		if (response.getStatusCode() != 200) {
			throw new AcceptanceTestException(String.format("Unable to create user with username %s.", username));
		}
	}
	

	@Override
	public Response updateUser(String username, String postJson) {
		RequestValues urlValues = createRequest(String.format("username : %s", username));
		JSON json = new JsonHashTranslator(translatedValues).translate(JSONSerializer.toJSON(postJson));
		String url = String.format("%s/users/%s", wsBaseUri, urlValues.get("username"));
		System.out.println(String.format("Calling [%s].", url));
		return put(url, body(json.toString(), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
	}	
	
	@Override
	public Response getUsers() {
		return get(wsBaseUri + "/users", ACCEPT_JSON_HEADER, createSessionHeader());
	}	

	@Override
	public Response getGetGamesFor(String username) {
		RequestValues urlValues = createRequest(String.format("username : %s", username));
		String url = String.format("%s/games?player=%s", wsBaseUri, urlValues.get("username"));
		return get(url, ACCEPT_JSON_HEADER, createSessionHeader());
	}	
	
	@Override
	public Response getGamesInState(String state) {
		String url = String.format("%s/games?state=%s", wsBaseUri, state);
		return get(url, ACCEPT_JSON_HEADER, createSessionHeader());
	}
	
	@Override
	public Response getAllGames() {
		String url = String.format("%s/games", wsBaseUri);
		return get(url, ACCEPT_JSON_HEADER, createSessionHeader());
	}	
	
	protected RequestValues createTranslatedUserRequest(String username,
			String password) {
		return getTranslatedValues().translateRequestValues(new RequestValues().add("username", username).add("password", password));
	}		
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#deleteUser(java.lang.String)
	 */
	@Override
	public void deleteUser(String data) {
		RequestValues requestValues = createRequest(DefaultValues.USER, data);
		Response response = delete(wsBaseUri + "/users/" + requestValues.get("username"), ACCEPT_JSON_HEADER, createSessionHeader());
	}
	
	//** GAME ADMIN ONLY ** 
	//
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#orderDeck(java.lang.String, java.util.List)
	 */
	@Override
	public void orderDeck(String gameId, List<Integer> order) {
		Response response = put(wsBaseUri + "/games/" + gameId + "/deck", body(JSONSerializer.toJSON(order).toString(), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
	}	
	
	@Override
	public void orderTariff(String gameId, List<Integer> order) {
		Response response = put(wsBaseUri + "/games/" + gameId + "/tariffs", body(JSONSerializer.toJSON(order).toString(), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
	}
	
	//** GAME METHODS ** //
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#createGame(java.lang.String)
	 */
	@Override
	public Response createGame(String data) {
		RequestValues requestValues = createRequest(data);
		Response response = post(wsBaseUri + "/games", body(requestValues.get("username"), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
		return rememberGame(response);
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#joinGame(java.lang.String, java.lang.String)
	 */
	@Override
	public Response joinGame(String gameId, String data) {
		this.gameId = gameId;
		RequestValues requestValues = createRequest(data);
		return post(wsBaseUri + "/games/" + gameId + "/players", body(requestValues.get("username"), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#startGame()
	 */
	@Override
	public Response startGame() {
		return put(wsBaseUri + "/games/" + gameId + "/state", body("PLAYING", JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#chooseRole(java.lang.String, java.lang.String)
	 */
	@Override
	public Response chooseRole(String urlData, String postData) {
		RequestValues urlValues = createRequest(urlData);
		RequestValues postValues = createRequest(postData);
		String url = String.format("%s/games/%s/rounds/%s/phases/%s/role", wsBaseUri, gameId, urlValues.get("round"), urlValues.get("phase"));
		return put(url, body(postValues.toJson(), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#makePlayChoice(java.lang.String, java.lang.String)
	 */
	@Override
	public Response makePlayChoice(String urlData, String postJson) {
		RequestValues urlValues = createRequest(urlData);
		JSON json = new JsonHashTranslator(translatedValues).translate(JSONSerializer.toJSON(postJson));
//		if (urlValues.get("round").equals("11") && urlValues.get("phase").equals("3") && urlValues.get("play").equals("1")) {
//			System.out.println("here");
//		}
		String url = String.format("%s/games/%s/rounds/%s/phases/%s/plays/%s/decision", wsBaseUri, gameId, urlValues.get("round"), urlValues.get("phase"), urlValues.get("play"));
		System.out.println(String.format("Calling [%s].", url));
		return put(url, body(json.toString(), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
	}
	
	@Override
	public Response makesGovernorPlay(String urlData, String postJson) {
		RequestValues urlValues = createRequest(urlData);
		JSON json = new JsonHashTranslator(translatedValues).translate(JSONSerializer.toJSON(postJson));
		String url = String.format("%s/games/%s/rounds/%s/governorChoice", wsBaseUri, gameId, urlValues.get("round"));
		System.out.println(String.format("Calling [%s].", url));
		return put(url, body(json.toString(), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
	}	
	
	
	private Response rememberGame(Response response) {
		gameId = extractGameId(response);
		return response;
	}

	private String extractGameId(Response response) {
		JSONObject json = (JSONObject)JSONSerializer.toJSON(response.asText());
		try {
			return json.getString("gameId");
		} catch (JSONException e) {
			throw new AcceptanceTestException(String.format("Unable to find gameId. Got response code [%d] and content [%s].", response.getStatusCode(), response.asText()), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#getGame()
	 */
	@Override
	public Response getGame() {
		return getGame(Long.valueOf(gameId));
	}
	
	@Override
	public Response getGame(long gameId) {
		Response response = get(wsBaseUri + "/games/" + gameId, ACCEPT_JSON_HEADER, createSessionHeader());
		return response;
		
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

	private AnyRequestModifier createSessionHeader() {
		return header("Cookie", "JSESSIONID=" + sessionId);
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#getTranslatedValues()
	 */
	@Override
	public TranslatedValues getTranslatedValues() {
		return translatedValues;
	}


	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#logout()
	 */
	@Override
	public void logout() {
		
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#getGameId()
	 */
	@Override
	public String getGameId() {
		return gameId;
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#deleteAnyGame()
	 */
	@Override
	public void deleteAnyGame() {
		if (gameId != null) {
			delete(wsBaseUri + "/games/" + gameId, ACCEPT_JSON_HEADER, createSessionHeader());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.GameDriverSession#addTranslatedValues(java.lang.String)
	 */
	@Override
	public void addTranslatedValues(String data) {
		RequestValues values = new RequestValues().addReadableData(data);
		for (Map.Entry<String, String> entry : values) {
			getTranslatedValues().add(entry.getKey(), entry.getValue());
		}
	}
}
