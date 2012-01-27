package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matcher;
import org.junit.Test;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class GameCreationAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	@Test
	public void testCanCreateGame() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice")),

				when(gameCreatedBy("#alice")),

				then(verifySuccessfulResponseContains("{ 'players' : [ { 'name' : '#alice', victoryPoints: 0 } ] }")));
	}

	@Test
	public void testCanJoinGame() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice")).and(
						userExistsAndAuthenticated("#bob")).and(
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

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(userExistsAndAuthenticated("#charlie"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob"))
						.and(gameStartedBy("#alice")),

				when(gameOwnedByJoinedBy("#alice", "#charlie")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'NOT_RECRUITING' }")));
	}

	@Test
	public void testMaximumOf4Players() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(userExistsAndAuthenticated("#charlie"))
						.and(userExistsAndAuthenticated("#debbie"))
						.and(userExistsAndAuthenticated("#eric"))
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

		given(userExistsAndAuthenticated("#alice"))
				.and(gameCreatedBy("#alice")),

		when(gameOwnedByJoinedBy("#alice", "#alice")),

		then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'ALREADY_PLAYER' }")));
	}

	@Test
	public void testAttemptToJoinGameTwice() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameOwnedByJoinedBy("#alice", "#bob")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(verifyResponseContains("{ code : 'ALREADY_PLAYER' }")));
	}

	@Test 
	public void testCannotStartSomeoneElsesGame() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameStartedBy("#bob")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED)).and(verifyResponseContains("{ code : 'NOT_CORRECT_USER' }")));
	}

	@Test
	public void testCanStartGame() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameStartedBy("#alice")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'AWAITING_ROLE_CHOICE' } } }")));
	}

	@Test
	public void testCannotStartGameIfFewerThanTwoPlayers() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice")).and(
						userExistsAndAuthenticated("#bob")).and(
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

				given(userExistsAndAuthenticated("#alice")),

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
	
}
