package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.GivenBddParts.given;
import static com.github.dansmithy.bdd.BddHelper.then;
import static com.github.dansmithy.bdd.BddHelper.when;
import static com.github.dansmithy.driver.BddPartProvider.gameBegunWithTwoPlayers;
import static com.github.dansmithy.driver.BddPartProvider.roleChosenBy;
import static com.github.dansmithy.driver.BddPartProvider.userPlays;
import static com.github.dansmithy.driver.BddPartProvider.verifySuccessfulResponseContains;

import org.junit.Ignore;
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
	@Ignore
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
	@Ignore
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
	@Ignore
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
								"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#smithy', payment : [ '#indigoplant6' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : COUNCILLOR"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ councilDiscarded : [ ] }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : PROSPECTOR"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1",
								"{ }"))
						.and(userPlays("#bob",
								"round : 1; phase : 3; play : 2",
								"{ 'skip' : true }"))
								
						.and(roleChosenBy("#bob", "round : 2; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#bob",
								"round : 2; phase : 1; play : 1",
								"{ build : '#coffeeroaster', payment : [ '#indigoplant7', '#indigoplant8' ] }"))
						.and(userPlays("#alice",
								"round : 2; phase : 1; play : 2",
								"{ build : '#quarry', payment : [ '#indigoplant5', '#sugarmill3', '#sugarmill4', '#sugarmill5' ] }"))
						.and(roleChosenBy("#alice", "round : 2; phase : 2",
								"role : COUNCILLOR"))
						.and(userPlays(
								"#alice",
								"round : 2; phase : 2; play : 1",
								"{ councilDiscarded : [ '#sugarmill7', '#sugarmill8', '#tobaccostorage2' ] }"))
						.and(userPlays("#bob",
								"round : 2; phase : 2; play : 2",
								"{ councilDiscarded : [ '#tobaccostorage4' ] }"))
						.and(roleChosenBy("#bob", "round : 2; phase : 3",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 2; phase : 3; play : 1",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						.and(userPlays("#alice",
								"round : 2; phase : 3; play : 2",
								"{ productionFactories : [ '#indigoplant' ] }"))
						.and(roleChosenBy("#alice", "round : 3; phase : 1",
								"role : TRADER"))
								
						.and(userPlays(
								"#alice",
								"round : 3; phase : 1; play : 1",
								"{ productionFactories : [ '#indigoplant' ] }"))
						.and(userPlays("#bob",
								"round : 3; phase : 1; play : 2",
								"{ productionFactories : [ '#coffeeroaster' ] }"))
						.and(roleChosenBy("#bob", "round : 3; phase : 2",
								"role : BUILDER"))
						.and(userPlays("#bob",
								"round : 3; phase : 2; play : 1",
								"{ build : '#well', payment : [ '#tobaccostorage3' ] }"))
						.and(userPlays("#alice",
								"round : 3; phase : 2; play : 2",
								"{ build : '#sugarmill6', payment : [ '#tobaccostorage', '#tobaccostorage8' ] }"))
						.and(roleChosenBy("#alice", "round : 3; phase : 3",
								"role : PRODUCER"))
						.and(userPlays("#alice",
								"round : 3; phase : 3; play : 1",
								"{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
						.and(userPlays("#bob",
								"round : 3; phase : 3; play : 2",
								"{ productionFactories : [ '#coffeeroaster' ] }"))
								
						.and(roleChosenBy("#bob", "round : 4; phase : 1",
								"role : TRADER"))
						.and(userPlays(
								"#bob",
								"round : 4; phase : 1; play : 1",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						.and(userPlays("#alice",
								"round : 4; phase : 1; play : 2",
								"{ productionFactories : [ '#sugarmill6' ] }"))
						.and(roleChosenBy("#alice", "round : 4; phase : 2",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 4; phase : 2; play : 1",
								"{ build : '#carpenter', payment : [ '#silversmelter' ] }"))
						.and(userPlays("#bob",
								"round : 4; phase : 2; play : 2",
								"{ build : '#aqueduct', payment : [ '#coffeeroaster2', '#coffeeroaster3', '#coffeeroaster7' ] }"))						
						.and(roleChosenBy("#bob", "round : 4; phase : 3",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 4; phase : 3; play : 1",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						.and(userPlays("#alice",
								"round : 4; phase : 3; play : 2",
								"{ productionFactories : [ '#sugarmill6' ] }"))
								
						.and(roleChosenBy("#alice", "round : 5; phase : 1",
								"role : TRADER"))
						.and(userPlays(
								"#alice",
								"round : 5; phase : 1; play : 1",
								"{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
						.and(userPlays("#bob",
								"round : 5; phase : 1; play : 2",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						.and(roleChosenBy("#bob", "round : 5; phase : 2",
								"role : BUILDER"))
						.and(userPlays("#bob",
								"round : 5; phase : 2; play : 1",
								"{ build : '#tradingpost', payment : [ '#aqueduct3' ] }"))
						.and(userPlays("#alice",
								"round : 5; phase : 2; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#alice", "round : 5; phase : 3",
								"role : PRODUCER"))
						.and(userPlays("#alice",
								"round : 5; phase : 3; play : 1",
								"{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
						.and(userPlays("#bob",
								"round : 5; phase : 3; play : 2",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))								

						.and(roleChosenBy("#bob", "round : 6; phase : 1",
								"role : TRADER"))
						.and(userPlays(
								"#bob",
								"round : 6; phase : 1; play : 1",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						.and(userPlays("#alice",
								"round : 6; phase : 1; play : 2",
								"{ productionFactories : [ '#indigoplant' ] }"))
						.and(roleChosenBy("#alice", "round : 6; phase : 2",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 6; phase : 2; play : 1",
								"{ build : '#markethall', payment : [ '#aqueduct2', '#quarry2' ] }"))
						.and(userPlays("#bob",
								"round : 6; phase : 2; play : 2",
								"{ build : '#marketstand', payment : [ '#carpenter2', '#carpenter3' ] }"))						
						.and(roleChosenBy("#bob", "round : 6; phase : 3",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 6; phase : 3; play : 1",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						.and(userPlays("#alice",
								"round : 6; phase : 3; play : 2",
								"{ productionFactories : [ '#indigoplant' ] }"))

						.and(roleChosenBy("#alice", "round : 7; phase : 1",
								"role : TRADER"))
						.and(userPlays(
								"#alice",
								"round : 7; phase : 1; play : 1",
								"{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
						.and(userPlays("#bob",
								"round : 7; phase : 1; play : 2",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						.and(roleChosenBy("#bob", "round : 7; phase : 2",
								"role : BUILDER"))
						.and(userPlays("#bob",
								"round : 7; phase : 2; play : 1",
								"{ build : '#triumphalarch', payment : [ '#coffeeroaster8', '#prefecture2', '#prefecture3', '#smithy3', '#well3' ] }"))
						.and(userPlays("#alice",
								"round : 7; phase : 2; play : 2",
								"{ build : '#archive', payment : [] }"))
						.and(roleChosenBy("#alice", "round : 7; phase : 3",
								"role : PRODUCER"))
						.and(userPlays("#alice",
								"round : 7; phase : 3; play : 1",
								"{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
						.and(userPlays("#bob",
								"round : 7; phase : 3; play : 2",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))								

						.and(roleChosenBy("#bob", "round : 8; phase : 1",
								"role : TRADER"))
						.and(userPlays(
								"#bob",
								"round : 8; phase : 1; play : 1",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						.and(userPlays("#alice",
								"round : 8; phase : 1; play : 2",
								"{ productionFactories : [ '#sugarmill6' ] }"))
						.and(roleChosenBy("#alice", "round : 8; phase : 2",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 8; phase : 2; play : 1",
								"{ build : '#cityhall', payment : [ '#tradingpost3', '#well2', '#archive3', '#goldmine3' ] }"))
						.and(userPlays("#bob",
								"round : 8; phase : 2; play : 2",
								"{ build : '#statue', payment : [ '#statue2', '#statue3', '#archive2' ] }"))						
						.and(roleChosenBy("#bob", "round : 8; phase : 3",
								"role : PROSPECTOR"))
						.and(userPlays("#bob",
								"round : 8; phase : 3; play : 1",
								"{ }"))
						.and(userPlays("#alice",
								"round : 8; phase : 3; play : 2",
								"{ skip : true }"))								

						.and(roleChosenBy("#alice", "round : 9; phase : 1",
								"role : BUILDER"))
						.and(userPlays(
								"#alice",
								"round : 9; phase : 1; play : 1",
								"{ build : '#poorhouse', payment : [ ] }"))
						.and(userPlays("#bob",
								"round : 9; phase : 1; play : 2",
								"{ build : '#victorycolumn', payment : [ '#chapel2', '#chapel3', '#crane2', '#crane3' ] }"))
						.and(roleChosenBy("#bob", "round : 9; phase : 2",
								"role : PROSPECTOR"))
						.and(userPlays("#bob",
								"round : 9; phase : 2; play : 1",
								"{ }"))
						.and(userPlays("#alice",
								"round : 9; phase : 2; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#alice", "round : 9; phase : 3",
								"role : TRADER"))
						.and(userPlays("#alice",
								"round : 9; phase : 3; play : 1",
								"{ productionFactories : [ '#indigoplant' ] }"))
						.and(userPlays("#bob",
								"round : 9; phase : 3; play : 2",
								"{ skip : true }"))

						.and(roleChosenBy("#bob", "round : 10; phase : 1",
								"role : PRODUCER"))
						.and(userPlays(
								"#bob",
								"round : 10; phase : 1; play : 1",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						.and(userPlays("#alice",
								"round : 10; phase : 1; play : 2",
								"{ productionFactories : [ '#sugarmill6' ] }"))
						.and(roleChosenBy("#alice", "round : 10; phase : 2",
								"role : TRADER"))
						.and(userPlays("#alice",
								"round : 10; phase : 2; play : 1",
								"{ productionFactories : [ '#sugarmill6' ] }"))
						.and(userPlays("#bob",
								"round : 10; phase : 2; play : 2",
								"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))						
						.and(roleChosenBy("#bob", "round : 10; phase : 3",
								"role : BUILDER"))
						.and(userPlays("#bob",
								"round : 10; phase : 3; play : 1",
								"{ build : '#goldmine2', payment : [ ] }"))
						.and(userPlays("#alice",
								"round : 10; phase : 3; play : 2",
								"{ build : '#library', payment : [ '#tower2', '#tower3', '#goldmine', '#victorycolumn3' ] }"))
								
						.and(roleChosenBy("#alice", "round : 11; phase : 1",
								"role : BUILDER"))
						.and(userPlays(
								"#alice",
								"round : 11; phase : 1; play : 1",
								"{ build : '#hero', payment : [ '#silversmelter8', '#hero3' ] }"))
								,

				when(userPlays("#bob",
						"round : 11; phase : 1; play : 2",
				"{ build : '#palace', payment : [ '#blackmarket', '#victorycolumn2', '#silversmelter5', '#library2', '#library3', '#hero2' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'COMPLETED', 'winner' : '#alice', 'players^name' : [ { 'name' : '#alice', victoryPoints: 29 }, { 'name' : '#bob', victoryPoints: 28 } ], 'roundNumber' : 11 }")));
	}	

}
