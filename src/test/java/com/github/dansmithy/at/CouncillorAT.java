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

				then(verifySuccessfulResponseContains("{ 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT', 'offered' : { 'councilOffered' : [ '#well', '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ], 'councilRetainCount' : 1, 'councilCanDiscardHandCards' : false } } ] } ] } ] }")));
	}

	@Test
	public void testCanCouncilCards() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays^state : [ { 'state' : 'COMPLETED', 'playChoice' : { 'councilDiscarded' : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] } } ] } ] } ] }")));
	}

	@Test
	public void testCannotDiscardCardDoNotOwn() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#silversmelter' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(verifyResponseContains("{ code : 'NOT_OWNED_COUNCIL_DISCARD' }")));
	}
	
	@Test
	public void testCannotDiscardTooFewCards() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill'] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(verifyResponseContains("{ code : 'UNDER_DISCARD' }")));
	}
	
	@Test
	public void testCannotDiscardTooManyCards() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [  '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2', '#well' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(verifyResponseContains("{ code : 'OVER_DISCARD' }")));
	}		
	
	@Test
	public void testCannotCheatAndDiscardSameCardTwice() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [  '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(verifyResponseContains("{ code : 'DUPLICATE_CHOICE' }")));
	}		
	
	@Test
	public void testCannotDiscardHandCardsWithoutArchive() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(verifyResponseContains("{ code : 'NOT_OWNED_COUNCIL_DISCARD' }")));
	}

}
