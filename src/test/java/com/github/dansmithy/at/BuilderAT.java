package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.given;
import static com.github.dansmithy.bdd.BddHelper.then;
import static com.github.dansmithy.bdd.BddHelper.when;
import static com.github.dansmithy.driver.BddPartProvider.gameCreatedBy;
import static com.github.dansmithy.driver.BddPartProvider.gameOwnedByJoinedBy;
import static com.github.dansmithy.driver.BddPartProvider.gameStartedBy;
import static com.github.dansmithy.driver.BddPartProvider.orderDeckOwnedBy;
import static com.github.dansmithy.driver.BddPartProvider.roleChosenBy;
import static com.github.dansmithy.driver.BddPartProvider.userExistsAndAuthenticated;
import static com.github.dansmithy.driver.BddPartProvider.userPlays;
import static com.github.dansmithy.driver.BddPartProvider.verifyResponseCodeIs;
import static com.github.dansmithy.driver.BddPartProvider.verifySuccessfulResponseContains;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.DeckOrder;
import com.github.dansmithy.driver.GameDriver;

public class BuilderAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

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
