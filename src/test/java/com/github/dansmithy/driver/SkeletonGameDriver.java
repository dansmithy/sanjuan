package com.github.dansmithy.driver;

import static com.github.restdriver.serverdriver.RestServerDriver.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dansmithy.sanjuan.exception.AccessUnauthorizedRuntimeException;
import com.github.dansmithy.sanjuan.exception.ResourceNotFoundRuntimeException;
import com.github.restdriver.serverdriver.http.response.Response;

public abstract class SkeletonGameDriver implements GameDriver {

	private Map<String, GameDriverSession> playerSessions = new HashMap<String, GameDriverSession>();
	private List<String> players = new ArrayList<String>();
	private TranslatedValues translatedValues;
	private Response lastResponse;
	private Map<String, Response> rememberedResponses = new HashMap<String, Response>();
	
	private String adminUsername;
	
	public SkeletonGameDriver(String adminUsername) {
		super();
		this.adminUsername = adminUsername;
		this.translatedValues = new TranslatedValues();
	}

	protected void createAdminSession() {
		GameDriverSession adminSession = login(adminUsername, true);
		playerSessions.put(adminUsername, adminSession);
	}

	protected RequestValues createTranslatedUserRequest(String username) {
		return getTranslatedValues().aliasRequestValues(new RequestValues().add("username", username));
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.BddContext#getAdminSession()
	 */
	@Override
	public GameDriverSession getAdminSession() {
		return getSession(adminUsername);
	}
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.BddContext#getSession(java.lang.String)
	 */
	@Override
	public GameDriverSession getSession(String username) {
		return playerSessions.get(username);
	}	
	
	protected abstract GameDriverSession login(String username, boolean isAdmin);
	

	@Override
	public void loginUser(String username) {
		GameDriverSession sessionPlayer = login(username, false);
		players.add(username);
		playerSessions.put(username, sessionPlayer);
	}
		

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.BddContext#cleanup()
	 */
	@Override
	public void cleanup() {
		for (String user : players) {
			if (!user.equals(adminUsername)) {
				if (playerSessions.containsKey(user)) {
					deleteGame(playerSessions.get(user).getGameId());
				}
			}
		}
		getAdminSession().logout();
	}

	private void deleteGame(String gameId) {
		if (gameId != null) {
			try {
				getAdminSession().deleteGame(gameId);
			} catch (ResourceNotFoundRuntimeException e) {
				// do nothing! only for spring implementaiton
			} catch (AccessUnauthorizedRuntimeException e) {
				// do nothing! only for spring implementaiton
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.BddContext#getLastResponse()
	 */
	@Override
	public Response getLastResponse() {
		return lastResponse;
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.BddContext#setLastResponse(com.github.restdriver.serverdriver.http.response.Response)
	 */
	@Override
	public void setLastResponse(Response lastResponse) {
		this.lastResponse = lastResponse;
	}

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.BddContext#getTranslatedValues()
	 */
	@Override
	public TranslatedValues getTranslatedValues() {
		return translatedValues;
	}
	
	@Override
	public void outputGamePlayers() {
		StringBuilder builder = new StringBuilder();
		String delimiter = "";
		for (String user : playerSessions.keySet()) {
			if (!user.equals(adminUsername)) {
				builder.append(delimiter).append(translatedValues.get(user));
			}
			delimiter = ", ";
		}
		System.out.println(String.format("Started a game with players: %s.", builder.toString()));
	}
	
	@Override
	public Response getRememberedResponse(String rememberedResponseKey) {
		return rememberedResponses.get(rememberedResponseKey);
	}

	@Override
	public void rememberLastResponse(String rememberedResponseKey) {
		rememberedResponses.put(rememberedResponseKey, getLastResponse());
	}	
}
