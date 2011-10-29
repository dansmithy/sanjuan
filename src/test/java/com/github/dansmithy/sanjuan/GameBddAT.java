package com.github.dansmithy.sanjuan;

import static com.github.dansmithy.bdd.BddHelper.given;
import static com.github.dansmithy.bdd.BddHelper.then;
import static com.github.dansmithy.bdd.BddHelper.when;
import static com.github.dansmithy.driver.BddPartProvider.gameCreatedBy;
import static com.github.dansmithy.driver.BddPartProvider.gameOwnedByContains;
import static com.github.dansmithy.driver.BddPartProvider.gameOwnedByJoinedBy;
import static com.github.dansmithy.driver.BddPartProvider.gameStartedBy;
import static com.github.dansmithy.driver.BddPartProvider.orderDeckOwnedBy;
import static com.github.dansmithy.driver.BddPartProvider.roleChosenBy;
import static com.github.dansmithy.driver.BddPartProvider.userExistsAndAuthenticated;
import static com.github.dansmithy.driver.BddPartProvider.userPlays;
import static com.github.dansmithy.driver.BddPartProvider.verifyResponseCodeIs;
import static com.github.dansmithy.driver.BddPartProvider.verifySuccessfulResponseContains;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;

import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.DeckOrder;
import com.github.dansmithy.driver.GameDriver;

public class GameBddAT {

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

				then(verifyResponseCodeIs(HTTP_CONFLICT)));
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

				then(verifyResponseCodeIs(HTTP_CONFLICT)));
	}

	@Test
	public void testAttemptToJoinOwnGame() {

		bdd.runTest(

		given(userExistsAndAuthenticated("#alice"))
				.and(gameCreatedBy("#alice")),

		when(gameOwnedByJoinedBy("#alice", "#alice")),

		then(verifyResponseCodeIs(HTTP_CONFLICT)));
	}

	@Test
	public void testAttemptToJoinGameTwice() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameOwnedByJoinedBy("#alice", "#bob")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)));
	}

	@Test
	public void testCanStartGame() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob")),

				when(gameStartedBy("#alice")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'AWAITING_ROLE_CHOICE' } ] } ] }")));
	}

	@Test
	public void testCannotStartGameIfFewerThanTwoPlayers() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice")).and(
						userExistsAndAuthenticated("#bob")).and(
						gameCreatedBy("#alice")),

				when(gameStartedBy("#alice")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)));
	}

	@Test
	public void testStartGameIfAlreadyPlaying() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob"))
						.and(gameStartedBy("#alice")),

				when(gameStartedBy("#alice")),

				then(verifyResponseCodeIs(HTTP_OK)));
	}

	@Test
	public void testCanChooseRole() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob"))
						.and(gameStartedBy("#alice")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : BUILDER")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT' } ] } ] } ] }")));
	}

	@Test
	public void testCanBuildForCorrectPrice() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(orderDeckOwnedBy("#alice", DeckOrder.Order1))
						.and(gameOwnedByJoinedBy("#alice", "#bob"))
						.and(gameStartedBy("#alice"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"build : #coffeeroaster; payment : #aqueduct,#marketstand,#tradingpost")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays^state : [ { 'state' : 'COMPLETED' } ] } ] } ] }")));
	}

	@Test
	public void testCannotUnderpay() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(orderDeckOwnedBy("#alice", DeckOrder.Order1))
						.and(gameOwnedByJoinedBy("#alice", "#bob"))
						.and(gameStartedBy("#alice"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"build : #coffeeroaster; payment : #aqueduct,#marketstand")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)));
	}

	@Test
	public void testCanOnlyBuildOwnedCards() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(orderDeckOwnedBy("#alice", DeckOrder.Order1))
						.and(gameOwnedByJoinedBy("#alice", "#bob"))
						.and(gameStartedBy("#alice"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"build : #quarry; payment : #coffeeroaster,#aqueduct,#marketstand")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)));
	}

}
