package com.github.dansmithy.sanjuan.driver;

import com.github.restdriver.serverdriver.http.response.Response;

public interface GameDriver {

//	GameDriverSession login(String data, TranslatedValues translatedValues);
//
//	GameDriverSession login(String data,
//			TranslatedValues translatedValues, boolean isAdmin);

	GameDriverSession getAdminSession();

	void loginUser(String username);

	void cleanup();

	GameDriverSession getSession(String username);

	Response getLastResponse();

	void setLastResponse(Response lastResponse);

	TranslatedValues getTranslatedValues();
}