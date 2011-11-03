package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.GivenBddParts.given;
import static com.github.dansmithy.bdd.BddHelper.then;
import static com.github.dansmithy.bdd.BddHelper.when;
import static com.github.dansmithy.driver.BddPartProvider.gameBegunWithTwoPlayers;
import static com.github.dansmithy.driver.BddPartProvider.roleChosenBy;
import static com.github.dansmithy.driver.BddPartProvider.userPlays;
import static com.github.dansmithy.driver.BddPartProvider.verifyResponseCodeIs;
import static com.github.dansmithy.driver.BddPartProvider.verifySuccessfulResponseContains;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class CouncillorAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	@Test
	public void testCorrectNumberOfCardsOffered() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : COUNCILLOR")),

				then(verifySuccessfulResponseContains("{ 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT', 'offered' : { 'councilOffered' : [ '#carpenter', '#sugarmill', '#goldmine', '#poorhouse', '#archive' ], 'councilRetainCount' : 1, 'councilCanDiscardHandCards' : false } } ] } ] } ] }")));
	}

	@Test
	public void testCanCouncilCards() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#goldmine' , '#poorhouse', '#archive'] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays^state : [ { 'state' : 'COMPLETED', 'playChoice' : { 'councilDiscarded' : [ '#goldmine', '#poorhouse', '#archive' ] } } ] } ] } ] }")));
	}

	/**
	 * Alice hand is: #coffeeroaster, #aqueduct, #marketstand, #tradingpost,
	 * #prefecture. Council options are: '#carpenter', '#sugarmill', '#goldmine', '#poorhouse', '#archive'.
	 */
	@Test
	public void testCannotDiscardCardDoNotOwn() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#archive', '#carpenter', '#statue' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)));
	}
	
	/**
	 * Alice hand is: #coffeeroaster, #aqueduct, #marketstand, #tradingpost,
	 * #prefecture. Council options are: '#carpenter', '#sugarmill', '#goldmine', '#poorhouse', '#archive'.
	 */
	@Test
	public void testCannotDiscardHandCardsWithoutArchive() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#archive', '#carpenter', '#aqueduct' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)));
	}	

}
