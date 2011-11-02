package com.github.dansmithy.driver;

import java.util.List;

import com.github.restdriver.serverdriver.http.response.Response;

public interface GameDriverSession {

	void createUser(String username);

	void deleteUser(String data);

	//** GAME ADMIN ONLY ** 
	//
	void orderDeck(String gameId, List<Integer> order);
	void orderTariff(String gameId, List<Integer> order);

	Response createGame(String data);

	Response joinGame(String gameId, String data);

	Response startGame();

	Response chooseRole(String urlData, String postData);

	Response makePlayChoice(String urlData, String postData);

	Response getGame();

//	Response getGame(String gameId);

	TranslatedValues getTranslatedValues();

	void logout();

	String getGameId();

	void deleteAnyGame();

	void addTranslatedValues(String data);


}