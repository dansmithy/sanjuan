package com.github.dansmithy.sanjuan.driver;


public class GameDriver {

	public GameDriverSession login(String data) {
		TranslatedValues translatedValues = new TranslatedValues();
		RequestValues requestValues = translatedValues.translateRequestValues(new RequestValues().addAll(DefaultValues.USER).addReadableData(data));
		
		System.out.println("login with " + requestValues.toJson());
		// login with req
		String sessionId = "session123";
		return new GameDriverSession(sessionId, translatedValues);
	}
	
}
