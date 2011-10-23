package com.github.dansmithy.sanjuan;

import org.junit.Before;
import org.junit.Test;

import com.github.dansmithy.sanjuan.driver.DefaultValues;
import com.github.dansmithy.sanjuan.driver.GameDriver;
import com.github.dansmithy.sanjuan.driver.GameDriverSession;
import com.github.dansmithy.sanjuan.driver.RequestValues;
import com.github.dansmithy.sanjuan.driver.TranslatedValues;
import com.github.dansmithy.sanjuan.json.JsonCompare;
import com.github.dansmithy.sanjuan.json.JsonStringCompare;

public class GameAT {

	private GameDriver driver = new GameDriver();
	private JsonStringCompare jsonStringCompare = new JsonStringCompare(new JsonCompare());
	private GameDriverSession sessionPlayer1;
	private GameDriverSession sessionPlayer2;
	
	@Before
	public void setup() {
		GameDriverSession adminSession = driver.login("username : testAdmin, password : testPassword");
		adminSession.createUser("username : #alice");
		adminSession.createUser("username : #bob");
		adminSession.logout();
		sessionPlayer1 = driver.login("username : #alice");
		sessionPlayer2 = driver.login("username : #bob");
	}
	
	@Test
	public void testCanCreateGame() {

		String game = sessionPlayer1.createGame("name : #alice");
		
		String expectedGame = "{ 'players^name' : [ { 'name' : '#alice', victoryPoints: 30 } ] }";
		jsonStringCompare.equalsNoOrphans(game, expectedGame);
	}
	
	@Test
	public void testCanJoinGame() {
		sessionPlayer1.createGame("name : #alice");
		sessionPlayer2.joinGame(sessionPlayer1.getGameId());
		// test game has 
		
	}
	
	@Test // requires deck modify code
	public void testHandsAreDealt() {
		
		// needs 2 sessions;
		String game = sessionPlayer1.createGame("name : #alice");
		
		String expectedGame = "{ 'players^name' : [ { 'name' : '#alice', victoryPoints: 30 } ] }";
		jsonStringCompare.equalsNoOrphans(game, expectedGame);
		
	}
	
	@Test
	public void testRequestCreate() {
		TranslatedValues translatedValues = new TranslatedValues();
		RequestValues values = new RequestValues().addAll(DefaultValues.USER).addReadableData("username : #danny, password : testPassword");
		RequestValues newValues = translatedValues.translateRequestValues(values);
		System.out.println(newValues.toJson());
		
		RequestValues values2 = new RequestValues().addReadableData("user : #danny");
		RequestValues values3 = translatedValues.translateRequestValues(values2);
		System.out.println(values3.toJson());
		
	}
	

}
