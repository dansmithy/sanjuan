package com.github.dansmithy.sanjuan;

import static com.github.dansmithy.sanjuan.json.JsonMatchers.containsJson;
import static com.github.dansmithy.sanjuan.json.JsonMatchers.whenTranslatedBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.dansmithy.sanjuan.driver.GameDriver;
import com.github.dansmithy.sanjuan.driver.GameDriverSession;
import com.github.restdriver.serverdriver.http.response.Response;

public class GameAT {

	private static final String ADMIN_ACCOUNT = String.format("username : %s, password : %s", "danny", "danny");
	
	private GameDriver driver = new GameDriver("http://localhost:8086");
	private GameDriverSession adminSession;
	private GameDriverSession sessionPlayer1;
	private GameDriverSession sessionPlayer2;
	
	@Before
	public void setup() {
		adminSession = driver.login(ADMIN_ACCOUNT);
		adminSession.createUser("username : #alice");
		adminSession.createUser("username : #bob");
		sessionPlayer1 = driver.login("username : #alice", adminSession.getTranslatedValues());
		sessionPlayer2 = driver.login("username : #bob", adminSession.getTranslatedValues());
	}
	
	@After
	public void clearUp() {
		adminSession.deleteUser("username : #alice");
		adminSession.deleteUser("username : #bob");
		adminSession.logout();
		sessionPlayer1.deleteAnyGame();
	}
	
	@Test
	public void testCanCreateGame() {

		Response actualResponse = sessionPlayer1.createGame("username : #alice");
		String expectedGame = "{ 'players' : [ { 'name' : '#alice', victoryPoints: 0 } ] }";
		
		Assert.assertThat(actualResponse.getStatusCode(), is(equalTo(200)));
		Assert.assertThat(actualResponse.asText(), containsJson(expectedGame, whenTranslatedBy(sessionPlayer1.getTranslatedValues())));
	}
	
	@Test
	public void testCanJoinGame() {
		sessionPlayer1.createGame("username : #alice");
		Response actualResponse = sessionPlayer2.joinGame(sessionPlayer1.getGameId(), "username : #bob");
		String expectedPlayer = "{ 'name' : '#bob' }";
		
		Assert.assertThat(actualResponse.getStatusCode(), is(equalTo(200)));
		Assert.assertThat(actualResponse.asText(), containsJson(expectedPlayer, whenTranslatedBy(sessionPlayer2.getTranslatedValues())));
		
	}
	
	//@Test // requires deck modify code
	public void testHandsAreDealt() {
		
		// needs 2 sessions;
		Response actualResponse = sessionPlayer1.createGame("username : #alice");
		String expectedGame = "{ 'players^name' : [ { 'name' : '#alice', victoryPoints: 30 } ] }";
		Assert.assertThat(actualResponse.asText(), containsJson(expectedGame, whenTranslatedBy(sessionPlayer1.getTranslatedValues())));
		
	}
	

}
