package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.GivenBddParts;
import com.github.dansmithy.driver.GameDriver;

public class ExtendedPlayAT extends BaseAT {
	
	@Test
	public void testCannotDoOtherPlay() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 2; play : 1",
						"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(
						verifyResponseContains("{ code : 'PLAY_NOT_ACTIVE' }")));
	}
	
	@Test
	public void testCannotMakePlayIfNotPlayerInGame() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")).and(userAuthenticated("#charlie")).and(copyGameIdBetweenUsers("#alice", "#charlie")),

				when(userPlays("#charlie", "round : 1; phase : 1; play : 1",
						"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED)).and(verifyResponseContains("{ code : 'NOT_YOUR_GAME' }")));
	}
	

	@Test
	public void testCompletePhase() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#smithy', payment : [ '#indigoplant6' ] }")),

				when(roleChosenBy("#bob", "round : 1; phase : 2",
						"role : PRODUCER")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3 }, { 'name' : '#bob', victoryPoints: 2 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING', currentPlay : { 'state' : 'AWAITING_INPUT', 'offered' : { 'goodsCanProduce' : 2, 'factoriesCanProduce' : [ '#indigoplant2' ] } } } } }")));
	}

	@Test
	public void testCompleteTwoPhases() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#smithy', payment : [ '#indigoplant6' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : COUNCILLOR"))
						.and(userPlays(
								"#bob",
								"round : 1; phase : 2; play : 1",
								"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ councilDiscarded : [ ] }")),

				when(roleChosenBy("#alice", "round : 1; phase : 3",
						"role : TRADER")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3 }, { 'name' : '#bob', victoryPoints: 2 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING', currentPlay : { 'state' : 'AWAITING_INPUT', 'offered' : { 'goodsCanTrade' : 2 } } } } }")));
	}

	@Test
	public void testCompleteRound() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#smithy', payment : [ '#indigoplant6' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : COUNCILLOR"))
						.and(userPlays(
								"#bob",
								"round : 1; phase : 2; play : 1",
								"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ councilDiscarded : [ ] }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : PROSPECTOR"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1", "{ }"))
						.and(userPlays("#bob",
								"round : 1; phase : 3; play : 2",
								"{ 'skip' : true }")),

				when(roleChosenBy("#bob", "round : 2; phase : 1",
						"role : PROSPECTOR")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3 }, { 'name' : '#bob', victoryPoints: 2 } ], 'roundNumber' : 2, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING', currentPlay : { 'state' : 'AWAITING_INPUT' } } } }")));
	}

	@Test
	public void testCanCompleteGameWithASkipAndDecidedByVictoryPoints() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						gameAlmostCompleted()),

				when(finalLosingMove()),

				then(verifySuccessfulResponseContains("{ 'state' : 'COMPLETED', 'winner' : '#bob', 'players^name' : [ { 'name' : '#alice', victoryPoints: 29 }, { 'name' : '#bob', victoryPoints: 30 } ], 'roundNumber' : 12 }")));
	}
	
	@Test
	public void testCanCompleteGameWithASkipAndDecidedByHandCards() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						gameAlmostCompleted()),

				when(finalWinningMove()),

				then(verifySuccessfulResponseContains("{ 'state' : 'COMPLETED', 'winner' : '#alice', 'players^name' : [ { 'name' : '#alice', victoryPoints: 30 }, { 'name' : '#bob', victoryPoints: 30 } ], 'roundNumber' : 12 }")));
	}	

	@Test
	public void testCannotStartGameOnceCompleted() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						gameCompleted()),

				when(gameStartedBy("#alice")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(
						verifyResponseContains("{ code : 'NOT_RECRUITING' }")));
	}

	@Test
	public void testCannotChooseRoleOnceGameCompleted() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						gameCompleted()),

				when(roleChosenBy("#bob", "round : 11; phase : 2",
						"role : COUNCILLOR")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(
						verifyResponseContains("{ code : 'NOT_PLAYING' }")));
	}

	@Test
	public void testCannotChooseRoleInCheekyWayOnceGameCompleted() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						gameCompleted()),

				when(roleChosenBy("#bob", "round : 0; phase : 0",
						"role : COUNCILLOR")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(
						verifyResponseContains("{ code : 'NOT_PLAYING' }")));
	}

	@Test
	public void testCannotMakePlayInCheekyWayOnceGameCompleted() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						gameCompleted()),

				when(userPlays("#bob", "round : 0; phase : 0; play : 0",
						"{ skip : 'true' }")),

				then(verifyResponseCodeIs(HTTP_CONFLICT)).and(
						verifyResponseContains("{ code : 'NOT_PLAYING' }")));
	}
	
	@Test
	public void testEndedDateIsNotEmpty() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						gameAlmostCompleted()),

				when(finalLosingMove()),

				then(verifyJsonPath("$.ended", is(not(nullValue())))));
	}	

	private BddPart<GameDriver> gameCompleted() {
		return new GivenBddParts(gameAlmostCompleted()).and(finalLosingMove());
	}

	private BddPart<GameDriver> finalLosingMove() {
		return userPlays("#alice", "round : 12; phase : 1; play : 2",
				"{ skip : true }");
	}

	private BddPart<GameDriver> finalWinningMove() {
		return userPlays("#alice", "round : 12; phase : 1; play : 2",
				"{ build : '#indigoplant5', payment : [ '#palace2' ] }");
	}

	private BddPart<GameDriver> gameAlmostCompleted() {
		return new GivenBddParts(roleChosenBy("#alice", "round : 1; phase : 1",
				"role : BUILDER"))
				.and(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }"))
				.and(userPlays("#bob", "round : 1; phase : 1; play : 2",
						"{ build : '#smithy', payment : [ '#indigoplant6' ] }"))
				.and(roleChosenBy("#bob", "round : 1; phase : 2",
						"role : COUNCILLOR"))
				.and(userPlays(
						"#bob",
						"round : 1; phase : 2; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] }"))
				.and(userPlays("#alice", "round : 1; phase : 2; play : 2",
						"{ councilDiscarded : [ ] }"))
				.and(roleChosenBy("#alice", "round : 1; phase : 3",
						"role : PROSPECTOR"))
				.and(userPlays("#alice", "round : 1; phase : 3; play : 1",
						"{ }"))
				.and(userPlays("#bob", "round : 1; phase : 3; play : 2",
						"{ 'skip' : true }"))

				.and(roleChosenBy("#bob", "round : 2; phase : 1",
						"role : BUILDER"))
				.and(userPlays("#bob", "round : 2; phase : 1; play : 1",
						"{ build : '#coffeeroaster', payment : [ '#indigoplant7', '#indigoplant8' ] }"))
				.and(userPlays(
						"#alice",
						"round : 2; phase : 1; play : 2",
						"{ build : '#quarry', payment : [ '#silversmelter7', '#sugarmill3', '#sugarmill4', '#sugarmill5' ] }"))
				.and(roleChosenBy("#alice", "round : 2; phase : 2",
						"role : COUNCILLOR"))
				.and(userPlays("#alice", "round : 2; phase : 2; play : 1",
						"{ councilDiscarded : [ '#sugarmill7', '#sugarmill8', '#tobaccostorage2' ] }"))
				.and(userPlays("#bob", "round : 2; phase : 2; play : 2",
						"{ councilDiscarded : [ '#tobaccostorage4' ] }"))
				.and(roleChosenBy("#bob", "round : 2; phase : 3",
						"role : PRODUCER"))
				.and(userPlays("#bob", "round : 2; phase : 3; play : 1",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
				.and(userPlays("#alice", "round : 2; phase : 3; play : 2",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(roleChosenBy("#alice", "round : 3; phase : 1",
						"role : TRADER"))

				.and(userPlays("#alice", "round : 3; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(userPlays("#bob", "round : 3; phase : 1; play : 2",
						"{ productionFactories : [ '#coffeeroaster' ] }"))
				.and(roleChosenBy("#bob", "round : 3; phase : 2",
						"role : BUILDER"))
				.and(userPlays("#bob", "round : 3; phase : 2; play : 1",
						"{ build : '#well', payment : [ '#tobaccostorage3' ] }"))
				.and(userPlays(
						"#alice",
						"round : 3; phase : 2; play : 2",
						"{ build : '#sugarmill6', payment : [ '#tobaccostorage', '#tobaccostorage8' ] }"))
				.and(roleChosenBy("#alice", "round : 3; phase : 3",
						"role : PRODUCER"))
				.and(userPlays("#alice", "round : 3; phase : 3; play : 1",
						"{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
				.and(userPlays("#bob", "round : 3; phase : 3; play : 2",
						"{ productionFactories : [ '#coffeeroaster' ] }"))

				.and(roleChosenBy("#bob", "round : 4; phase : 1",
						"role : TRADER"))
				.and(userPlays("#bob", "round : 4; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
				.and(userPlays("#alice", "round : 4; phase : 1; play : 2",
						"{ productionFactories : [ '#sugarmill6' ] }"))
				.and(roleChosenBy("#alice", "round : 4; phase : 2",
						"role : BUILDER"))
				.and(userPlays("#alice", "round : 4; phase : 2; play : 1",
						"{ build : '#carpenter', payment : [ '#silversmelter' ] }"))
				.and(userPlays(
						"#bob",
						"round : 4; phase : 2; play : 2",
						"{ build : '#aqueduct', payment : [ '#coffeeroaster2', '#coffeeroaster3', '#coffeeroaster7' ] }"))
				.and(roleChosenBy("#bob", "round : 4; phase : 3",
						"role : PRODUCER"))
				.and(userPlays("#bob", "round : 4; phase : 3; play : 1",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
				.and(userPlays("#alice", "round : 4; phase : 3; play : 2",
						"{ productionFactories : [ '#sugarmill6' ] }"))

				.and(roleChosenBy("#alice", "round : 5; phase : 1",
						"role : TRADER"))
				.and(userPlays("#alice", "round : 5; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
				.and(userPlays("#bob", "round : 5; phase : 1; play : 2",
						"{ productionFactories : [ '#coffeeroaster' ] }"))
				.and(roleChosenBy("#bob", "round : 5; phase : 2",
						"role : BUILDER"))
				.and(userPlays("#bob", "round : 5; phase : 2; play : 1",
						"{ build : '#tradingpost', payment : [ '#aqueduct3' ] }"))
				.and(userPlays("#alice", "round : 5; phase : 2; play : 2",
						"{ skip : true }"))
				.and(roleChosenBy("#alice", "round : 5; phase : 3",
						"role : PRODUCER"))
				.and(userPlays("#alice", "round : 5; phase : 3; play : 1",
						"{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
				.and(userPlays("#bob", "round : 5; phase : 3; play : 2",
						"{ productionFactories : [ '#coffeeroaster' ] }"))

				.and(roleChosenBy("#bob", "round : 6; phase : 1",
						"role : TRADER"))
				.and(userPlays("#bob", "round : 6; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
				.and(userPlays("#alice", "round : 6; phase : 1; play : 2",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(roleChosenBy("#alice", "round : 6; phase : 2",
						"role : BUILDER"))
				.and(userPlays("#alice", "round : 6; phase : 2; play : 1",
						"{ build : '#markethall', payment : [ '#aqueduct2', '#quarry2' ] }"))
				.and(userPlays("#bob", "round : 6; phase : 2; play : 2",
						"{ build : '#marketstand', payment : [ '#carpenter2', '#carpenter3' ] }"))
				.and(roleChosenBy("#bob", "round : 6; phase : 3",
						"role : PRODUCER"))
				.and(userPlays("#bob", "round : 6; phase : 3; play : 1",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
				.and(userPlays("#alice", "round : 6; phase : 3; play : 2",
						"{ productionFactories : [ '#indigoplant' ] }"))

				.and(roleChosenBy("#alice", "round : 7; phase : 1",
						"role : TRADER"))
				.and(userPlays("#alice", "round : 7; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
				.and(userPlays("#bob", "round : 7; phase : 1; play : 2",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
				.and(roleChosenBy("#bob", "round : 7; phase : 2",
						"role : BUILDER"))
				.and(userPlays(
						"#bob",
						"round : 7; phase : 2; play : 1",
						"{ build : '#triumphalarch', payment : [ '#coffeeroaster8', '#statue2', '#prefecture3', '#smithy3', '#well3' ] }"))
				.and(userPlays("#alice", "round : 7; phase : 2; play : 2",
						"{ build : '#archive', payment : [] }"))
				.and(roleChosenBy("#alice", "round : 7; phase : 3",
						"role : PRODUCER"))
				.and(userPlays("#alice", "round : 7; phase : 3; play : 1",
						"{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
				.and(userPlays("#bob", "round : 7; phase : 3; play : 2",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))

				.and(roleChosenBy("#bob", "round : 8; phase : 1",
						"role : TRADER"))
				.and(userPlays("#bob", "round : 8; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
				.and(userPlays("#alice", "round : 8; phase : 1; play : 2",
						"{ productionFactories : [ '#sugarmill6' ] }"))
				.and(roleChosenBy("#alice", "round : 8; phase : 2",
						"role : BUILDER"))
				.and(userPlays(
						"#alice",
						"round : 8; phase : 2; play : 1",
						"{ build : '#cityhall', payment : [ '#tradingpost3', '#well2', '#archive3', '#goldmine3' ] }"))
				.and(userPlays("#bob", "round : 8; phase : 2; play : 2",
						"{ build : '#statue3', payment : [ '#chapel2', '#chapel3', '#archive2' ] }"))
				.and(roleChosenBy("#bob", "round : 8; phase : 3",
						"role : PROSPECTOR"))
				.and(userPlays("#bob", "round : 8; phase : 3; play : 1", "{ }"))
				.and(userPlays("#alice", "round : 8; phase : 3; play : 2",
						"{ skip : true }"))

				.and(roleChosenBy("#alice", "round : 9; phase : 1",
						"role : BUILDER"))
				.and(userPlays("#alice", "round : 9; phase : 1; play : 1",
						"{ build : '#poorhouse', payment : [ ] }"))
				.and(userPlays(
						"#bob",
						"round : 9; phase : 1; play : 2",
						"{ build : '#victorycolumn', payment : [ '#goldmine2', '#blackmarket', '#crane2', '#crane3' ] }"))
				.and(roleChosenBy("#bob", "round : 9; phase : 2",
						"role : PROSPECTOR"))
				.and(userPlays("#bob", "round : 9; phase : 2; play : 1", "{ }"))
				.and(userPlays("#alice", "round : 9; phase : 2; play : 2",
						"{ skip : true }"))
				.and(roleChosenBy("#alice", "round : 9; phase : 3",
						"role : TRADER"))
				.and(userPlays("#alice", "round : 9; phase : 3; play : 1",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(userPlays("#bob", "round : 9; phase : 3; play : 2",
						"{ skip : true }"))

				.and(roleChosenBy("#bob", "round : 10; phase : 1",
						"role : PRODUCER"))
				.and(userPlays("#bob", "round : 10; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
				.and(userPlays("#alice", "round : 10; phase : 1; play : 2",
						"{ productionFactories : [ '#sugarmill6' ] }"))
				.and(roleChosenBy("#alice", "round : 10; phase : 2",
						"role : TRADER"))
				.and(userPlays("#alice", "round : 10; phase : 2; play : 1",
						"{ productionFactories : [ '#sugarmill6' ] }"))
				.and(userPlays("#bob", "round : 10; phase : 2; play : 2",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
				.and(roleChosenBy("#bob", "round : 10; phase : 3",
						"role : BUILDER"))
				.and(userPlays(
						"#bob",
						"round : 10; phase : 3; play : 1",
						"{ build : '#palace', payment : [ '#victorycolumn2', '#silversmelter5', '#library2', '#library3', '#hero2' ] }"))
				.and(userPlays(
						"#alice",
						"round : 10; phase : 3; play : 2",
						"{ build : '#library', payment : [ '#tower2', '#tower3', '#goldmine', '#victorycolumn3' ] }"))

				.and(roleChosenBy("#alice", "round : 11; phase : 1",
						"role : BUILDER"))
				.and(userPlays("#alice", "round : 11; phase : 1; play : 1",
						"{ build : '#hero', payment : [ '#silversmelter8', '#hero3' ] }"))
				.and(userPlays("#bob", "round : 11; phase : 1; play : 2",
						"{ skip : true }"))
				.and(roleChosenBy("#bob", "round : 11; phase : 2",
						"role : PROSPECTOR"))
				.and(userPlays("#bob", "round : 11; phase : 2; play : 1",
						"{ }"))
				.and(userPlays("#alice", "round : 11; phase : 2; play : 2",
						"{ skip : true }"))
				.and(roleChosenBy("#alice", "round : 11; phase : 3",
						"role : PRODUCER"))
				.and(userPlays("#alice", "round : 11; phase : 3; play : 1",
						"{ productionFactories : [ '#sugarmill6' ] }"))
				.and(userPlays("#bob", "round : 11; phase : 3; play : 2",
						"{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
						
				.and(roleChosenBy("#bob", "round : 12; phase : 1",
						"role : BUILDER"))
				.and(userPlays("#bob", "round : 12; phase : 1; play : 1",
						"{ build : '#tobaccostorage6', payment : [ '#tower' ] }"))
						
						
						;
	}

}
