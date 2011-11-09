package com.github.dansmithy.driver;

import java.util.HashMap;
import java.util.Map;

import com.github.restdriver.serverdriver.http.response.Response;

public abstract class SkeletonGameDriver implements GameDriver {

	private Map<String, GameDriverSession> playerSessions = new HashMap<String, GameDriverSession>();
	private TranslatedValues translatedValues;
	private Response lastResponse;
	
	private String adminUsername;
	private String adminPassword;
	
	public SkeletonGameDriver(String adminUsername, String adminPassword) {
		super();
		this.adminUsername = adminUsername;
		this.adminPassword = adminPassword;
		this.translatedValues = new TranslatedValues();
	}

	protected void createAdminSession() {
		GameDriverSession adminSession = login(adminUsername, adminPassword);
		playerSessions.put(adminUsername, adminSession);
	}

	protected RequestValues createTranslatedUserRequest(String username,
			String password) {
		return getTranslatedValues().translateRequestValues(new RequestValues().add("username", username).add("password", password));
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
	
	protected abstract GameDriverSession login(String username, String password);
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.BddContext#loginUser(java.lang.String)
	 */
	@Override
	public void loginUser(String username) {
		loginUser(username, DefaultValues.PASSWORD);
	}
	
	@Override
	public void loginUser(String username, String password) {
		GameDriverSession sessionPlayer = login(username, password);
		playerSessions.put(username, sessionPlayer);
	}
		

	/* (non-Javadoc)
	 * @see com.github.dansmithy.sanjuan.driver.BddContext#cleanup()
	 */
	@Override
	public void cleanup() {
		for (String user : playerSessions.keySet()) {
			if (!user.equals(adminUsername)) {
				getSession(user).deleteAnyGame();
				getAdminSession().deleteUser(String.format("username : %s", user));
			}
		}
		getAdminSession().logout();
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

}
