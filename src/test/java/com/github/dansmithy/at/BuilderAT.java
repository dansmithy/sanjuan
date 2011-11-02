package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.given;
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

public class BuilderAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	@Test
	public void testCorrectDiscountsOffered() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : BUILDER")),

				then(verifySuccessfulResponseContains("{ 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT', 'offered' : { 'builderDiscountOnProduction' : 1, 'builderDiscountOnViolet' : 1 } } ] } ] } ] }")));
	}
	
	@Test
	public void testCanBuildForCorrectPrice() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ build : '#coffeeroaster', payment : [ '#aqueduct', '#marketstand', '#tradingpost' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays^state : [ { 'state' : 'COMPLETED', 'playChoice' : { 'build' : '#coffeeroaster', payment : [ '#aqueduct', '#marketstand', '#tradingpost' ] } } ] } ] } ] }")));
	}

	@Test
	public void testCannotUnderpay() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ build : '#coffeeroaster', payment : [ '#aqueduct', '#marketstand' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)));
	}

	/**
	 * Alice hand is: #coffeeroaster, #aqueduct, #marketstand, #tradingpost,
	 * #prefecture
	 */
	@Test
	public void testCannotOverpay() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays(
						"#alice",
						"round : 1; phase : 1; play : 1",
						"{ build : '#coffeeroaster', payment : [ '#aqueduct', '#marketstand', '#tradingpost', '#prefecture' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)));
	}

	@Test
	public void testCanOnlyBuildOwnedCards() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ build : '#quarry', payment : [ '#coffeeroaster', '#aqueduct', '#marketstand' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)));
	}
	
	@Test
	public void testSmithyGivesProductionDiscount() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays(
								"#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#coffeeroaster', payment : [ '#aqueduct', '#marketstand', '#tradingpost' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#smithy', payment : [ '#library' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ productionFactories : [ '#indigoplant2' ] }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ productionFactories : [ '#coffeeroaster' ] }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : TRADER"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1",
								"{ productionFactories : [ '#coffeeroaster' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 3; play : 2",
								"{ productionFactories : [ '#indigoplant2' ] }"))
						.and(roleChosenBy("#bob", "round : 2; phase : 1",
								"role : BUILDER"))
						.and(userPlays(
								"#alice",
								"round : 2; phase : 1; play : 1",
								"{ build : '#prefecture', payment : [ '#crane', '#chapel' ] }"))
						.and(userPlays(
								"#bob",
								"round : 2; phase : 1; play : 2",
								"{ build : '#well', payment : [ '#markethall', '#quarry' ] }")),

				when(roleChosenBy("#alice", "round : 2; phase : 2",
						"role : PROSPECTOR")),
								
				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 5 }, { 'name' : '#bob', victoryPoints: 3 } ], 'roundNumber' : 2, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT', 'offered' : null } ] } ] } ] }")));
		
		
	}


		

}
