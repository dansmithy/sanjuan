package com.github.dansmithy.sanjuan;

import static com.github.dansmithy.sanjuan.json.JsonMatchers.containsJson;
import static com.github.dansmithy.sanjuan.json.JsonMatchers.whenTranslatedBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.net.HttpURLConnection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.dansmithy.sanjuan.driver.DeckOrder;
import com.github.dansmithy.sanjuan.driver.GameDriver;
import com.github.dansmithy.sanjuan.driver.GameDriverSession;
import com.github.restdriver.serverdriver.http.response.Response;

public class GameAT {

	private static final String ADMIN_ACCOUNT = String.format("username : %s; password : %s", "danny", "danny");
	
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
	
	/**
	 * given(userExists("alice"))
	 * when(gameCreatedBy("alice"))
	 * then(checkResponseIs("..."))
	 */
	@Test
	public void testCanCreateGame() {

		
		Response actualResponse = sessionPlayer1.createGame("username : #alice");
		String expectedGame = "{ 'players' : [ { 'name' : '#alice', victoryPoints: 0 } ] }";
		
		Assert.assertThat(actualResponse.getStatusCode(), is(equalTo(200)));
		Assert.assertThat(actualResponse.asText(), containsJson(expectedGame, whenTranslatedBy(sessionPlayer1.getTranslatedValues())));
	}
	
	/**
	 * given(userExists("alice")).and(userExists("bob")).and(gameCreatedBy("alice"))
	 * when(gameJoinedBy("bob"))
	 * then(checkResponseIs("...").and(gameIsEqualTo("...")
	 */
	@Test
	public void testCanJoinGame() {
		sessionPlayer1.createGame("username : #alice");
		Response actualResponse = sessionPlayer2.joinGame(sessionPlayer1.getGameId(), "username : #bob");
		String expectedPlayer = "{ 'name' : '#bob' }";
		
		Assert.assertThat(actualResponse.getStatusCode(), is(equalTo(200)));
		Assert.assertThat(actualResponse.asText(), containsJson(expectedPlayer, whenTranslatedBy(sessionPlayer2.getTranslatedValues())));
		
		Response actualGameResponse = sessionPlayer1.getGame();
		String expectedGame = "{ 'state' : 'RECRUITING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 0 }, { 'name' : '#bob', victoryPoints: 0 } ] }";
		Assert.assertThat(actualGameResponse.asText(), containsJson(expectedGame, whenTranslatedBy(sessionPlayer1.getTranslatedValues())));
	}
	
	/**
	 * given(userExists("alice")).and(gameCreatedBy("alice"))
	 * when(gameJoinedBy("alice"))
	 * then(checkResponseIs("..."))
	 */
	@Test
	public void testAttemptToJoinOwnGame() {
		sessionPlayer1.createGame("username : #alice");
		Response actualResponse = sessionPlayer1.joinGame(sessionPlayer1.getGameId(), "username : #alice");
		
		Assert.assertThat(actualResponse.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_CONFLICT)));
	}
	
	/**
	 * given(userExists("alice")).and(userExists("bob")).and(gameCreatedBy("alice")).and(gameJoinedBy("bob"))
	 * when(gameJoinedBy("bob"))
	 * then(checkResponseIs("..."))
	 */
	@Test
	public void testAttemptToJoinGameTwice() {
		sessionPlayer1.createGame("username : #alice");
		sessionPlayer2.joinGame(sessionPlayer1.getGameId(), "username : #bob");
		
		Response actualResponse = sessionPlayer2.joinGame(sessionPlayer1.getGameId(), "username : #bob");
		
		Assert.assertThat(actualResponse.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_CONFLICT)));
	}		
	
	
	/**
	 * given(userExists("alice")).and(userExists("bob")).and(gameCreatedBy("alice")).and(gameJoinedBy("bob"))
	 * when(gameStartedBy("alice"))
	 * then(checkResponseIs("..."))
	 */
	@Test
	public void testCanStartGame() {
		sessionPlayer1.createGame("username : #alice");
		sessionPlayer2.joinGame(sessionPlayer1.getGameId(), "username : #bob");
		Response actualResponse = sessionPlayer1.startGame();
		
		String expectedGame = "{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'AWAITING_ROLE_CHOICE' } ] } ] }";
		
		Assert.assertThat(actualResponse.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));
		Assert.assertThat(actualResponse.asText(), containsJson(expectedGame, whenTranslatedBy(sessionPlayer1.getTranslatedValues())));
	}
	
	/**
	 * given(userExists("alice")).and(userExists("bob")).and(gameCreatedBy("alice")).and(gameJoinedBy("bob")).and(gameStartedBy("alice"))
	 * when(roleChosen("alice", "BUILDER"))
	 * then(checkResponseIs("..."))
	 */
	@Test
	public void testCanChooseRole() {
		sessionPlayer1.createGame("username : #alice");
		sessionPlayer2.joinGame(sessionPlayer1.getGameId(), "username : #bob");
		sessionPlayer1.startGame();
		
		Response actualResponse = sessionPlayer1.chooseRole("round : 1; phase : 1", "role : BUILDER");
		String expectedGame = "{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT' } ] } ] } ] }";
		
		Assert.assertThat(actualResponse.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));
		Assert.assertThat(actualResponse.asText(), containsJson(expectedGame, whenTranslatedBy(sessionPlayer1.getTranslatedValues())));
	}		
	
	/**
	 * given(userExists("alice")).and(userExists("bob")).and(gameCreatedBy("alice")).and(deckOrderSetTo("order1").and(gameJoinedBy("bob")).and(gameStartedBy("alice")).and(roleChosen("alice", "BUILDER"))
	 * when(doBuild("alice", "xx"))
	 * then(checkResponseIs("..."))
	 */
//	@Test
	public void testCanBuildCoffeeRoaster() {
		// this could be always there ... or maybe set by deck order?
		sessionPlayer1.addTranslatedValues("#coffeeroaster : xx");
		
		sessionPlayer1.createGame("username : #alice");
		adminSession.orderDeck(DeckOrder.order1());
		sessionPlayer2.joinGame(sessionPlayer1.getGameId(), "username : #bob");
		sessionPlayer1.startGame();
		sessionPlayer1.chooseRole("round : 1; phase : 1", "role : BUILDER");
		
		Response actualResponse = sessionPlayer1.makePlayChoice("build : #coffeeroaster; payment : #one,#two,#three");
		String expectedGame = "{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT' } ] } ] } ] }";
		
		Assert.assertThat(actualResponse.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));
		Assert.assertThat(actualResponse.asText(), containsJson(expectedGame, whenTranslatedBy(sessionPlayer1.getTranslatedValues())));
	}		
	
	//@Test // requires deck modify code
	public void testHandsAreDealt() {
		
		// needs 2 sessions;
		Response actualResponse = sessionPlayer1.createGame("username : #alice");
		String expectedGame = "{ 'players^name' : [ { 'name' : '#alice', victoryPoints: 30 } ] }";
		Assert.assertThat(actualResponse.asText(), containsJson(expectedGame, whenTranslatedBy(sessionPlayer1.getTranslatedValues())));
		
	}
	

}
