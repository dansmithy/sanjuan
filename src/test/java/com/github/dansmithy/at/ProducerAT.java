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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class ProducerAT {

	private static BddTestRunner<GameDriver> bdd;

	@BeforeClass
	public static void createTestRunner() {
		 bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();
	}
	
	@AfterClass
	public static void stopTestRunner() {
		bdd.shutdown();
	}
	
	@Test
	public void testCanChooseProducerRole() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : PRODUCER")),

				then(verifySuccessfulResponseContains("{ 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING', currentPlay : { 'state' : 'AWAITING_INPUT', 'offered' : { 'goodsCanProduce' : 2, 'factoriesCanProduce' : [ '#indigoplant' ] } } } } }")));
	}

	@Test
	public void testCanSelectFactoryToProduce() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : PRODUCER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING' } } }")));
		
		//currentPlay : [ { 'state' : 'COMPLETED', 'playChoice' : { 'productionFactories' : [ '#indigoplant' ] } } 
	}

	@Test
	public void testCannotProduceFactoryDoNotOwn() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : PRODUCER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ productionFactories : [ '#coffeeroaster' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'NOT_OWNED_FACTORY' }")));
	}

	@Test
	public void testCannotProduceMoreGoodsThanPermitted() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#indigoplant3', payment : [ ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ skip : true }")),

				when(userPlays("#alice", "round : 1; phase : 2; play : 2",
						"{ productionFactories : [ '#indigoplant', '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(
						verifyResponseContains("{ code : 'OVER_PRODUCE' }")));
	}

	@Test
	public void testCannotMistakenlyProduceSameFactoryTwice() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#indigoplant3', payment : [ ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#indigoplant6', payment : [ '#smithy' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : PRODUCER")),

				when(userPlays("#bob", "round : 1; phase : 2; play : 1",
						"{ productionFactories : [ '#indigoplant2', '#indigoplant2' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(
						verifyResponseContains("{ code : 'DUPLICATE_CHOICE' }")));
	}
	
	@Test
	public void testCannotProduceOnFactoryWithGoodAlready() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : PRODUCER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ productionFactories : [ '#indigoplant' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ productionFactories : [ '#indigoplant2' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : BUILDER"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ build : '#indigoplant6', payment : [ ] }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ build : '#indigoplant3', payment : [ '#indigoplant4' ] }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : PROSPECTOR"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1", "{ }"))
						.and(userPlays("#bob",
								"round : 1; phase : 3; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#bob", "round : 2; phase : 1",
								"role : PRODUCER")),

				when(userPlays("#bob", "round : 2; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant2', '#indigoplant6' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'NOT_EMPTY_FACTORY' }")));
	}	

}
