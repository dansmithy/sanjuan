package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.GivenBddParts.given;
import static com.github.dansmithy.bdd.BddHelper.then;
import static com.github.dansmithy.bdd.BddHelper.when;
import static com.github.dansmithy.driver.BddPartProvider.gameBegunWithTwoPlayers;
import static com.github.dansmithy.driver.BddPartProvider.roleChosenBy;
import static com.github.dansmithy.driver.BddPartProvider.userPlays;
import static com.github.dansmithy.driver.BddPartProvider.verifySuccessfulResponseContains;

import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class ExtendedPlayAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	/**
	 * Alice hand is: #coffeeroaster, #aqueduct, #marketstand, #tradingpost,
	 * #prefecture.
	 * 
	 * Bob's hand is: #quarry, #smithy, #markethall, #well, #library
	 */
	@Test
	public void testCompletePhase() {

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
								"{ build : '#smithy', payment : [ '#library' ] }")),

				when(roleChosenBy("#bob", "round : 1; phase : 2",
						"role : PRODUCER")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3 }, { 'name' : '#bob', victoryPoints: 2 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT', 'offered' : { 'goodsCanProduce' : 2, 'factoriesCanProduce' : [ '#indigoplant2' ] } } ] } ] } ] }")));
	}

	/**
	 * Alice hand is: #coffeeroaster, #aqueduct, #marketstand, #tradingpost,
	 * #prefecture.
	 * 
	 * Bob's hand is: #quarry, #smithy, #markethall, #well, #library
	 */
	@Test
	public void testCompleteTwoPhases() {

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
								"{ productionFactories : [ '#coffeeroaster' ] }")),

				when(roleChosenBy("#alice", "round : 1; phase : 3",
						"role : TRADER")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3 }, { 'name' : '#bob', victoryPoints: 2 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT', 'offered' : { 'goodsCanTrade' : 2 } } ] } ] } ] }")));
	}

	/**
	 * Alice hand is: #coffeeroaster, #aqueduct, #marketstand, #tradingpost,
	 * #prefecture.
	 * 
	 * Bob's hand is: #quarry, #smithy, #markethall, #well, #library
	 */
	@Test
	public void testCompleteRound() {

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
								"{ productionFactories : [ '#indigoplant2' ] }")),

				when(roleChosenBy("#bob", "round : 2; phase : 1",
						"role : PROSPECTOR")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3 }, { 'name' : '#bob', victoryPoints: 2 } ], 'roundNumber' : 2, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT', 'offered' : null } ] } ] } ] }")));
	}

	/**
	 * Alice hand is: #coffeeroaster, #aqueduct, #marketstand, #tradingpost,
	 * #prefecture.
	 * 
	 * Bob's hand is: #quarry, #smithy, #markethall, #well, #library
	 */
	@Test
	public void testFirstPhaseOfSecondRound() {

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
						.and(userPlays("#bob",
								"round : 2; phase : 1; play : 1",
								"{ build : '#well', payment : [ '#statue' ] }"))
						.and(userPlays("#alice",
								"round : 2; phase : 1; play : 2",
								"{ build : '#prefecture', payment : [ '#goldmine', '#poorhouse', '#archive' ] }"))
						.and(roleChosenBy("#alice", "round : 2; phase : 2",
								"role : COUNCILLOR"))
						.and(userPlays(
								"#alice",
								"round : 2; phase : 2; play : 1",
								"{ councilDiscarded : [ '#tower', '#victorycolumn', '#hero' ] }"))
						.and(userPlays("#bob",
								"round : 2; phase : 2; play : 2",
								"{ councilDiscarded : [ '#blackmarket' ] }"))
						.and(roleChosenBy("#bob", "round : 2; phase : 3",
								"role : PROSPECTOR"))
						.and(userPlays("#bob",
								"round : 2; phase : 3; play : 1",
								"{ }"))
						.and(userPlays("#alice",
								"round : 2; phase : 3; play : 2",
								"{ 'skip' : true }"))
								
								,

				when(roleChosenBy("#alice", "round : 3; phase : 1",
						"role : PROSPECTOR")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 5 }, { 'name' : '#bob', victoryPoints: 3 } ], 'roundNumber' : 3, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT', 'offered' : null } ] } ] } ] }")));
	}	

}
