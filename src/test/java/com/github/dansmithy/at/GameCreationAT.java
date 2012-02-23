package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class GameCreationAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	@Test
	public void testCanCreateGame() {

		bdd.runTest(

				given(userAuthenticated("#alice")),

				when(gameCreatedBy("#alice")),

				then(verifySuccessfulResponseContains("{ 'players' : [ { 'name' : '#alice', victoryPoints: 0 } ] }")));
	}

	@Test
	public void testCanJoinGame() {

		bdd.runTest(

				given(userAuthenticated("#alice")).and(
						userAuthenticated("#bob")).and(
						gameCreatedBy("#alice")),

				when(gameOwnedByJoinedBy("#alice", "#bob")),

				then(verifySuccessfulResponseContains("{ 'name' : '#bob' }"))
						.and(gameOwnedByContains(
								"#alice",
								"{ 'state' : 'RECRUITING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 0 }, { 'name' : '#bob', victoryPoints: 0 } ] }")));
	}

	@Test
	public void testCannotJoinGameOncePlaying() {

		bdd.runTest(

				given(userAuthenticated("#alice"))
						.and(userAuthenticated("#bob"))
						.and(userAuthenticated("#charlie"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob"))
						.and(gameStartedBy("#alice")),

				when(gameOwnedByJoinedBy("#alice", "#charlie")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'NOT_RECRUITING' }")));
	}

	@Test
	public void testMaximumOf4Players() {

		bdd.runTest(

				given(userAuthenticated("#alice"))
						.and(userAuthenticated("#bob"))
						.and(userAuthenticated("#charlie"))
						.and(userAuthenticated("#debbie"))
						.and(userAuthenticated("#eric"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob"))
						.and(gameOwnedByJoinedBy("#alice", "#charlie"))
						.and(gameOwnedByJoinedBy("#alice", "#debbie")),

				when(gameOwnedByJoinedBy("#alice", "#eric")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'TOO_MANY_PLAYERS' }")));
	}

	@Test
	public void testAttemptToJoinOwnGame() {

		bdd.runTest(

			given(userAuthenticated("#alice"))
					.and(gameCreatedBy("#alice")),
	
			when(gameOwnedByJoinedBy("#alice", "#alice")),
	
			then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'ALREADY_PLAYER' }")));
	}

	@Test
	public void testAttemptToJoinGameTwice() {

		bdd.runTest(

				given(userAuthenticated("#alice"))
						.and(userAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameOwnedByJoinedBy("#alice", "#bob")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'ALREADY_PLAYER' }")));
	}

	@Test 
	public void testCannotStartSomeoneElsesGame() {

		bdd.runTest(

				given(userAuthenticated("#alice"))
						.and(userAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameStartedBy("#bob")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED)).and(verifyResponseContains("{ code : 'NOT_CORRECT_USER' }")));
	}
	
	@Test 
	public void testCannotGetAGameYouAreNotInvolvedWith() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(userAuthenticated("#charlie")),

				when(getGameOwnedBySomebodyElse("#charlie", "#alice")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED)).and(verifyResponseContains("{ code : 'NOT_YOUR_GAME' }")));
	}	

	@Test
	public void testCanStartGame() {

		bdd.runTest(

				given(userAuthenticated("#alice"))
						.and(userAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameStartedBy("#alice")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'AWAITING_ROLE_CHOICE' } } }")));
	}

	@Test
	public void testCannotStartGameIfFewerThanTwoPlayers() {

		bdd.runTest(

				given(userAuthenticated("#alice")).and(
						userAuthenticated("#bob")).and(
						gameCreatedBy("#alice")),

				when(gameStartedBy("#alice")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'NOT_ENOUGH_PLAYERS' }")));
	}

	@Test
	public void testStartGameIfAlreadyPlaying() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),
				
				when(gameStartedBy("#alice")),

				then(verifyResponseCodeIs(HTTP_OK)));
	}
	
	@Test
	public void testCreatedDateIsNotEmpty() {

		bdd.runTest(

				given(userAuthenticated("#alice")),

				when(gameCreatedBy("#alice")),

				then(verifyJsonPath("$.created", is(not(nullValue())))));
	}

	@Test
	public void testStartedDateIsNotEmpty() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),
				
				when(gameStartedBy("#alice")),

				then(verifyJsonPath("$.started", is(not(nullValue())))));
	}

	@Test
	public void testOwnerCanDeleteGameInRecruitingMode() {

		bdd.runTest(

				given(userAuthenticated("#alice"))
					.and(userAuthenticated("#bob"))
					.and(gameCreatedBy("#alice"))
					.and(gameOwnedByJoinedBy("#alice", "#bob")),


				when(gameDeletedBy("#alice")),

				then(verifyResponseCodeIs(HTTP_NO_CONTENT)).and(gameOwnedByContains("#alice", "{ code : 'NOT_FOUND' }")));
	}

	@Test
	public void testOwnerCannotDeleteGameWhenNotInRecruitingMode() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(gameDeletedBy("#alice")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'NOT_RECRUITING' }"))
						.and(gameOwnedByContains("#alice",
								"{ state : 'PLAYING' }")));
	}

	@Test
	public void testOtherUserCanQuitGameDuringRecruitment() {

		bdd.runTest(

				given(userAuthenticated("#alice"))
					.and(userAuthenticated("#bob"))
					.and(gameCreatedBy("#alice"))
					.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameQuitBy("#bob")),

				then(verifyResponseCodeIs(HTTP_NO_CONTENT)).and(gameOwnedByContains("#alice", "{ players.size : 1 }")));
	}
	
	@Test
	public void testOtherUserCannotQuitGameDuringPlay() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(gameQuitBy("#bob")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'NOT_RECRUITING' }")).and(gameOwnedByContains("#alice", "{ players.size : 2 }")));
	}
	
	@Test
	public void testGameOwnerCannotQuitGameDuringRecruitment() {

		bdd.runTest(

				given(userAuthenticated("#alice"))
					.and(userAuthenticated("#bob"))
					.and(gameCreatedBy("#alice"))
					.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameQuitBy("#alice")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'OWNER_CANNOT_QUIT' }")).and(gameOwnedByContains("#alice", "{ players.size : 2 }")));
	}	
	
	@Test
	public void testOwnerCanAbandonInProgressGame() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(gameAbandonedBy("#alice")),

				then(verifySuccessfulResponseContains("{ 'state' : 'ABANDONED', 'abandonedBy' : '#alice' }")));
	}
	
	@Test
	public void testNotOnlyOwnerCanAbandonInProgressGame() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(gameAbandonedBy("#bob")),

				then(verifySuccessfulResponseContains("{ 'state' : 'ABANDONED', 'abandonedBy' : '#bob' }")));
	}	
	
	@Test
	public void testCannotAbandonGameInRecruitment() {

		bdd.runTest(

				given(userAuthenticated("#alice"))
					.and(userAuthenticated("#bob"))
					.and(gameCreatedBy("#alice"))
					.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameAbandonedBy("#bob")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'NOT_ACTIVE_STATE' }")).and(gameOwnedByContains("#alice", "{ players.size : 2 }")));
	}		
}
