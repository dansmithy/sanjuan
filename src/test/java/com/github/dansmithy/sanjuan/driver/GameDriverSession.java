package com.github.dansmithy.sanjuan.driver;

import static com.github.restdriver.serverdriver.RestServerDriver.body;
import static com.github.restdriver.serverdriver.RestServerDriver.delete;
import static com.github.restdriver.serverdriver.RestServerDriver.get;
import static com.github.restdriver.serverdriver.RestServerDriver.header;
import static com.github.restdriver.serverdriver.RestServerDriver.post;

import java.util.Collections;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.github.restdriver.serverdriver.http.AnyRequestModifier;
import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;

public class GameDriverSession {

	private static final String JSON_CONTENT_TYPE = "application/json";
	private static final Header ACCEPT_JSON_HEADER = header("Accept", JSON_CONTENT_TYPE);

	private final TranslatedValues translatedValues;
	private final String sessionId;
	private final String baseUri;
	private final String wsBaseUri;
	
	private String gameId;
	
	public GameDriverSession(String baseUri, String sessionId) {
		this(baseUri, sessionId, new TranslatedValues());
	}
	
	public GameDriverSession(String baseUri, String sessionId, TranslatedValues translatedValues) {
		this.baseUri = baseUri;
		this.sessionId = sessionId;
		this.translatedValues = translatedValues;
		this.wsBaseUri = String.format("%s/ws", baseUri);
	}

	//** USER METHODS ** //
	
	public void createUser(String data) {
		
		RequestValues requestValues = createRequest(DefaultValues.USER, data);
		Response response = post(wsBaseUri + "/users", body(requestValues.toJson(), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
	}
	
	public void deleteUser(String data) {
		RequestValues requestValues = createRequest(DefaultValues.USER, data);
		Response response = delete(wsBaseUri + "/users/" + requestValues.get("username"), ACCEPT_JSON_HEADER, createSessionHeader());
	}
	
	//** GAME METHODS ** //
	
	public Response createGame(String data) {
		RequestValues requestValues = createRequest(data);
		return rememberGame(post(wsBaseUri + "/games", body(requestValues.get("username"), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader()));
		
	}
	
	private Response rememberGame(Response response) {
		gameId = extractGameId(response);
		return response;
	}

	private String extractGameId(Response response) {
		JSONObject json = (JSONObject)JSONSerializer.toJSON(response.asText());
		return json.getString("gameId");
		
	}
	
	public Response getGame() {
		return getGame(gameId);
	}

	public Response getGame(String gameId) {
		Response response = get(wsBaseUri + "/games/" + gameId, ACCEPT_JSON_HEADER, createSessionHeader());
		return response;
	}
	
	public Response joinGame(String gameId, String data) {
		this.gameId = gameId;
		RequestValues requestValues = createRequest(data);
		return post(wsBaseUri + "/games/" + gameId + "/players", body(requestValues.get("username"), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER, createSessionHeader());
	}

	public void respond(String data) {
		RequestValues requestValues = createRequest(DefaultValues.USER, data);
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

	public TranslatedValues getTranslatedValues() {
		return translatedValues;
	}


	public void logout() {
		
	}

	public String getGameId() {
		return gameId;
	}

	public void deleteAnyGame() {
		if (gameId != null) {
			delete(wsBaseUri + "/games/" + gameId, ACCEPT_JSON_HEADER, createSessionHeader());
		}
	}



}
