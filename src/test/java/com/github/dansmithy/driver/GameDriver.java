package com.github.dansmithy.driver;

import com.github.restdriver.serverdriver.http.response.Response;

public interface GameDriver {

	GameDriverSession getAdminSession();

	void loginUser(String username);
	
	void cleanup();

	GameDriverSession getSession(String username);

	Response getLastResponse();

	void setLastResponse(Response lastResponse);

	TranslatedValues getTranslatedValues();

	void outputGamePlayers();

	Response getRememberedResponse(String rememberedResponseKey);

	void rememberLastResponse(String rememberedResponseKey);

	Response getCards();

	Response getCardTypes();

}