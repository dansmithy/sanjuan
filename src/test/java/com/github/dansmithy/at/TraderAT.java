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

public class TraderAT {

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
	public void testCanChooseTraderRole() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : PRODUCER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ productionFactories : [ '#indigoplant' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ productionFactories : [ '#indigoplant2' ] }")),

				when(roleChosenBy("#bob", "round : 1; phase : 2",
						"role : TRADER")),

				then(verifySuccessfulResponseContains("{ 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING', currentPlay : { 'state' : 'AWAITING_INPUT', 'offered' : { 'goodsCanTrade' : 2 } } } } }")));
	}

	@Test
	public void testCanChooseGoodsToTrade() {

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
								"role : TRADER")),

				when(userPlays("#bob", "round : 1; phase : 2; play : 1",
						"{ productionFactories : [ '#indigoplant2' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING' } } }")));
		
		//{ 'state' : 'COMPLETED', 'playChoice' : { 'productionFactories' : [ '#indigoplant2' ] } 
	}

	@Test
	public void testRewardedWithCorrectNumberOfCardsWhenTradeIndigo() {

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
								"role : TRADER")),

				when(userPlays("#bob", "round : 1; phase : 2; play : 1",
						"{ productionFactories : [ '#indigoplant2' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1, hand.size : 6 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING' } } }")));
		//{ 'state' : 'COMPLETED', 'playChoice' : { 'productionFactories' : [ '#indigoplant2' ] }
	}

	@Test
	public void testRewardedWithCorrectNumberOfCardsWhenTradeCoffeeRoaster() {
		
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#indigoplant3', payment : [ ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#coffeeroaster', payment : [ '#smithy', '#indigoplant6', '#indigoplant7', '#indigoplant8' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ productionFactories : [ '#indigoplant' ] }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : TRADER"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1",
								"{ skip : true }")),

				when(userPlays("#bob", "round : 1; phase : 3; play : 2",
						"{ productionFactories : [ '#coffeeroaster' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 2 }, { 'name' : '#bob', victoryPoints: 3, hand.size : 3 } ] }")));
	}

	@Test
	public void testCannotTradeWhenNoGood() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : TRADER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'NOT_FULL_FACTORY' }")));
	}

	@Test
	public void testCannotTradeFactoriesDoNotOwn() {

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
								"role : TRADER")),

				when(userPlays("#bob", "round : 1; phase : 2; play : 1",
						"{ productionFactories : [ '#silversmelter' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'NOT_OWNED_FACTORY' }")));
	}

	@Test
	public void testCannotMistakenlyTradeTheSameGoodTwice() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#indigoplant3', payment : [ ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#indigoplant6', payment : [ '#indigoplant7' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ productionFactories : [ '#indigoplant2', '#indigoplant6' ] }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ productionFactories : [ '#indigoplant' ] }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : PROSPECTOR"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1",
								"{ skip : true }"))
						.and(userPlays("#bob",
								"round : 1; phase : 3; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#bob", "round : 2; phase : 1",
								"role : TRADER")),

				when(userPlays("#bob", "round : 2; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant2', '#indigoplant2' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'DUPLICATE_CHOICE' }")));
	}

	@Test
	public void testCannotTradeMoreGoodsThanPrivilegeAllows() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#indigoplant3', payment : [ ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#indigoplant6', payment : [ '#indigoplant7' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ productionFactories : [ '#indigoplant2', '#indigoplant6' ] }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ productionFactories : [ '#indigoplant' ] }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : TRADER"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1",
								"{ skip : true }")),

				when(userPlays("#bob", "round : 1; phase : 3; play : 2",
						"{ productionFactories : [ '#indigoplant2', '#indigoplant6' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(
						verifyResponseContains("{ code : 'OVER_TRADE' }")));
	}
}
