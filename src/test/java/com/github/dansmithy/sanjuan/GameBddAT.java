package com.github.dansmithy.sanjuan;

import static com.github.dansmithy.sanjuan.bdd.BddHelper.given;
import static com.github.dansmithy.sanjuan.bdd.BddHelper.then;
import static com.github.dansmithy.sanjuan.bdd.BddHelper.when;
import static com.github.dansmithy.sanjuan.driver.BddPartProvider.deckOrdered;
import static com.github.dansmithy.sanjuan.driver.BddPartProvider.gameCreatedBy;
import static com.github.dansmithy.sanjuan.driver.BddPartProvider.gameOwnedByContains;
import static com.github.dansmithy.sanjuan.driver.BddPartProvider.gameOwnedByJoinedBy;
import static com.github.dansmithy.sanjuan.driver.BddPartProvider.gameStartedBy;
import static com.github.dansmithy.sanjuan.driver.BddPartProvider.roleChosenBy;
import static com.github.dansmithy.sanjuan.driver.BddPartProvider.userExistsAndAuthenticated;
import static com.github.dansmithy.sanjuan.driver.BddPartProvider.userPlays;
import static com.github.dansmithy.sanjuan.driver.BddPartProvider.verifyResponseCodeIs;
import static com.github.dansmithy.sanjuan.driver.BddPartProvider.verifySuccessfulResponseContains;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CONFLICT;

import org.junit.Test;

import com.github.dansmithy.sanjuan.driver.BddTestRunner;
import com.github.dansmithy.sanjuan.driver.DeckOrder;

public class GameBddAT {

	private static final String ADMIN_ACCOUNT = String.format("username : %s; password : %s", "danny", "danny");
	private BddTestRunner bdd = new BddTestRunner("http://localhost:8086", ADMIN_ACCOUNT);
	
	@Test
	public void testCanCreateGame() {

		bdd.runTest(
				
				given(userExistsAndAuthenticated("#alice")), 
				
				when(gameCreatedBy("#alice")),
				
				then(verifySuccessfulResponseContains("{ 'players' : [ { 'name' : '#alice', victoryPoints: 0 } ] }"))
				);
	}
	
	@Test
	public void testCanJoinGame() {
		
		bdd.runTest(
				
				given(userExistsAndAuthenticated("#alice")).and(userExistsAndAuthenticated("#bob")).and(gameCreatedBy("#alice")),
				
				when(gameOwnedByJoinedBy("#alice", "#bob")),
				
				then(verifySuccessfulResponseContains("{ 'name' : '#bob' }"))
					.and(gameOwnedByContains("#alice", "{ 'state' : 'RECRUITING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 0 }, { 'name' : '#bob', victoryPoints: 0 } ] }"))
				);
	}	
	
	@Test
	public void testAttemptToJoinOwnGame() {
		
		bdd.runTest(
				
				given(userExistsAndAuthenticated("#alice")).and(gameCreatedBy("#alice")),
				
				when(gameOwnedByJoinedBy("#alice", "#alice")),
				
				then(verifyResponseCodeIs(HTTP_CONFLICT))
				);
	}
	
	@Test
	public void testAttemptToJoinGameTwice() {
		
		bdd.runTest(
				
				given(userExistsAndAuthenticated("#alice")).and(userExistsAndAuthenticated("#bob")).and(gameCreatedBy("#alice")).and(gameOwnedByJoinedBy("#alice", "#bob")),
				
				when(gameOwnedByJoinedBy("#alice", "#bob")),
				
				then(verifyResponseCodeIs(HTTP_CONFLICT))
				);
	}		
	
	@Test
	public void testCanStartGame() {
		
		bdd.runTest(
				
				given(userExistsAndAuthenticated("#alice")).and(userExistsAndAuthenticated("#bob")).and(gameCreatedBy("#alice")).and(gameOwnedByJoinedBy("#alice", "#bob")),
				
				when(gameStartedBy("#alice")),
				
				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'AWAITING_ROLE_CHOICE' } ] } ] }"))
				);
	}
	
	@Test
	public void testCanChooseRole() {
		
		bdd.runTest(
				
				given(userExistsAndAuthenticated("#alice")).and(userExistsAndAuthenticated("#bob")).and(gameCreatedBy("#alice")).and(gameOwnedByJoinedBy("#alice", "#bob")).and(gameStartedBy("#alice")),
				
				when(roleChosenBy("#alice", "round : 1; phase : 1", "role : BUILDER")),
				
				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT' } ] } ] } ] }"))
				);
	}	
	
	@Test
	public void testCanBuildForCorrectPrice() {
		
		bdd.runTest(
				
				given(userExistsAndAuthenticated("#alice")).and(userExistsAndAuthenticated("#bob")).and(gameCreatedBy("#alice")).and(deckOrdered(DeckOrder.Order1)).and(gameOwnedByJoinedBy("#alice", "#bob")).and(gameStartedBy("#alice")).and(roleChosenBy("#alice", "round : 1; phase : 1", "role : BUILDER")),
				
				when(userPlays("#alice", "round : 1; phase : 1; play : 1", "build : #coffeeroaster; payment : #aqueduct,#marketstand,#tradingpost")),
				
				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays^state : [ { 'state' : 'COMPLETED' } ] } ] } ] }"))
				);
	}		
	
	@Test
	public void testCannotUnderpay() {
		
		bdd.runTest(
				
				given(userExistsAndAuthenticated("#alice")).and(userExistsAndAuthenticated("#bob")).and(gameCreatedBy("#alice")).and(deckOrdered(DeckOrder.Order1)).and(gameOwnedByJoinedBy("#alice", "#bob")).and(gameStartedBy("#alice")).and(roleChosenBy("#alice", "round : 1; phase : 1", "role : BUILDER")),
				
				when(userPlays("#alice", "round : 1; phase : 1; play : 1", "build : #coffeeroaster; payment : #aqueduct,#marketstand")),
				
				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
				);
	}
		
}
