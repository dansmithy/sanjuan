package com.github.dansmithy.sanjuan.driver;

import java.util.Map;

public class GameDriverSession {

	private final TranslatedValues translatedValues;
	private final String sessionId;
	
	public GameDriverSession(String sessionId) {
		this.sessionId = sessionId;
		translatedValues = new TranslatedValues();
	}
	
	public GameDriverSession(String sessionId, TranslatedValues translatedValues) {
		super();
		this.sessionId = sessionId;
		this.translatedValues = translatedValues;
	}

	public void logout() {
		
	}
	
	public void createUser(String data) {
		
		RequestValues requestValues = createRequest(DefaultValues.USER, data);
		System.out.println(requestValues.toJson());
		// do login
		
	}
	
	private Map<String, String> createUserDefaults() {
		return MapBuilder.simple().add("password", "testPassword").build();
	}	

	public String createGame() {
		return "{ 'gameId' : 45, 'deck' : {}, 'players' : [ { 'name' : 'xxx', victoryPoints: 30 } ] }";
	}

	public String createGame(String data) {
		RequestValues requestValues = createRequest(DefaultValues.USER, data);
		return null;
		
	}

	public Object getGameId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void joinGame(String gameId) {
	}

	public void joinGame(Object gameId) {
		// TODO Auto-generated method stub
		
	}
	
	public void respond(String data) {
		RequestValues requestValues = createRequest(DefaultValues.USER, data);
	}
	
	private RequestValues createRequest(Map<String, String> defaults, String data) {
		return translatedValues.translateRequestValues(new RequestValues().addAll(defaults).addReadableData(data));
	}

}
