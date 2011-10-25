package com.github.dansmithy.sanjuan.driver;

import java.util.HashMap;
import java.util.Map;

import com.github.restdriver.serverdriver.http.response.Response;

public class BddContext {

	private GameDriver driver;
	private GameDriverSession adminSession;
	private Map<String, GameDriverSession> playerSessions = new HashMap<String, GameDriverSession>();
	private TranslatedValues translatedValues;
	
	private Response lastResponse;

	public BddContext(String baseUrl, String adminDetails) {
		super();
		driver = new GameDriver(baseUrl);
		translatedValues = new TranslatedValues();
		adminSession = driver.login(adminDetails, translatedValues);
	}

	public GameDriverSession getAdminSession() {
		return adminSession;
	}

	public void loginUser(String username) {
		GameDriverSession sessionPlayer = driver.login(String.format("username : %s", username), translatedValues);
		playerSessions.put(username, sessionPlayer);
	}

	public void cleanup() {
		for (String user : playerSessions.keySet()) {
			playerSessions.get(user).deleteAnyGame();
			adminSession.deleteUser(String.format("username : %s", user));
		}
		adminSession.logout();
	}

	public GameDriverSession getSession(String username) {
		return playerSessions.get(username);
	}

	public Response getLastResponse() {
		return lastResponse;
	}

	public void setLastResponse(Response lastResponse) {
		this.lastResponse = lastResponse;
	}

	public TranslatedValues getTranslatedValues() {
		return translatedValues;
	}
	
}
