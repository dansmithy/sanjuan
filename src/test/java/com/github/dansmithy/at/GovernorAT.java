package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.GivenBddParts.given;
import static com.github.dansmithy.bdd.BddHelper.then;
import static com.github.dansmithy.bdd.BddHelper.when;
import static com.github.dansmithy.driver.BddPartProvider.gameBegunWithTwoPlayers;
import static com.github.dansmithy.driver.BddPartProvider.roleChosenBy;
import static com.github.dansmithy.driver.BddPartProvider.userPlays;
import static com.github.dansmithy.driver.BddPartProvider.verifyResponseCodeIs;
import static com.github.dansmithy.driver.BddPartProvider.verifyResponseContains;
import static com.github.dansmithy.driver.BddPartProvider.verifySuccessfulResponseContains;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class GovernorAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	@Test
	public void testCanChooseRole() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : BUILDER")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT' } ] } ] } ] }")));
	}

	@Test
	public void testCannotChooseRoleWhenNotYourTurn() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(roleChosenBy("#bob", "round : 1; phase : 1",
						"role : BUILDER")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED))
						.and(verifyResponseContains("{ code : 'NOT_CORRECT_USER' }")));
	}

	@Test
	public void testCannotChooseRoleWhenNotRoleChoiceTime() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : PROSPECTOR")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'PHASE_NOT_ACTIVE' }")));
	}

	@Test
	public void testCannotChooseAlreadyChosenRole() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#smithy', payment : [ '#indigoplant6' ] }")),

				when(roleChosenBy("#bob", "round : 1; phase : 2",
						"role : BUILDER")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'ROLE_ALREADY_TAKEN' }")));
	}
}
