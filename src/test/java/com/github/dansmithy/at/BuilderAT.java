package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.Test;

import com.github.dansmithy.driver.DeckOrder;

public class BuilderAT extends BaseAT {

	@Test
	public void testCorrectDiscountsOffered() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : BUILDER")),

				then(verifySuccessfulResponseContains("{ 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING', currentPlay : { 'state' : 'AWAITING_INPUT', 'offered' : { 'builderDiscountOnProduction' : 1, 'builderDiscountOnViolet' : 1 } } } } }")));
	}

	@Test
	public void testCanBuildForCorrectPrice() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING' } } }")));
		
		//currentPlay : { 'state' : 'COMPLETED', 'playChoice' : {  build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] } } 
	}

	@Test
	public void testCannotUnderpay() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ build : '#prefecture', payment : [ '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(
						verifyResponseContains("{ code : 'UNDERPAID' }")));
	}

	@Test
	public void testCannotOverpay() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays(
						"#alice",
						"round : 1; phase : 1; play : 1",
						"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4', '#silversmelter7' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(
						verifyResponseContains("{ code : 'OVERPAID' }")));
	}

	@Test
	public void testCanOnlyBuildOwnedCards() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ build : '#aqueduct', payment : [ '#indigoplant3', '#indigoplant4' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'NOT_OWNED_BUILD_CHOICE' }")));
	}

	@Test
	public void testCannotBuildWhenIsNotTurn() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#bob", "round : 1; phase : 1; play : 1",
						"{ build : '#smithy', payment : [ ] }")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED))
						.and(verifyResponseContains("{ code : 'NOT_CORRECT_USER' }")));
	}

	@Test
	public void testCanOnlyPayWithOwnedCards() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ build : '#prefecture', payment : [ '#indigoplant3', '#silversmelter' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'NOT_OWNED_PAYMENT' }")));
	}

	@Test
	public void testCanBuildSameProductionTwice() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
						gameBegunWithTwoPlayers("#alice", "#bob",
								withDeck(DeckOrder.Order2))).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")).and(
						userPlays("#alice", "round : 1; phase : 1; play : 1",
								"{ build : '#smithy', payment : [ ] }")),

				when(userPlays("#bob", "round : 1; phase : 1; play : 2",
						"{ build : '#indigoplant3', payment : [ '#coffeeroaster' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 2 }, { 'name' : '#bob', victoryPoints: 2 } ] }")));
	}

	@Test
	public void testCannotBuildSameVioletBuildingTwice() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
						gameBegunWithTwoPlayers("#alice", "#bob",
								withDeck(DeckOrder.Order2)))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#smithy', payment : [ ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#indigoplant3', payment : [ '#coffeeroaster' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ skip : true }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : TRADER"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1",
								"{ skip : true }"))
						.and(userPlays("#bob",
								"round : 1; phase : 3; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#bob", "round : 2; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#bob",
								"round : 2; phase : 1; play : 1",
								"{ build : '#indigoplant4', payment : [ ] }")),

				when(userPlays("#alice", "round : 2; phase : 1; play : 2",
						"{ build : '#smithy2', payment : [ '#quarry' ] }")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'BUILDING_ALREADY_BUILT' }")));
	}

	@Test
	public void testSmithyGivesProductionDiscount() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ skip : true }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#smithy', payment : [ '#indigoplant6' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ skip : true }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : TRADER"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1",
								"{ skip : true }"))
						.and(userPlays("#bob",
								"round : 1; phase : 3; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#bob", "round : 2; phase : 1",
								"role : BUILDER")),

				when(userPlays("#bob", "round : 2; phase : 1; play : 1",
						"{ build : '#coffeeroaster', payment : [ '#indigoplant7', '#indigoplant8' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 4 } ], 'roundNumber' : 2 }")));
	}
	
	@Test
	public void testDiscountGoesBelowZeroCanPayNothing() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ skip : true }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#smithy', payment : [ '#indigoplant6' ] }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ skip : true }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : TRADER"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1",
								"{ skip : true }"))
						.and(userPlays("#bob",
								"round : 1; phase : 3; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#bob", "round : 2; phase : 1",
								"role : BUILDER")),

				when(userPlays("#bob", "round : 2; phase : 1; play : 1",
						"{ build : '#indigoplant7', payment : [ ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 3 } ], 'roundNumber' : 2 }")));
	}	

	@Test
	public void testQuarryGivesVioletDiscount() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(
                        gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays(
								"#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#quarry', payment : [ '#indigoplant3', '#indigoplant4', '#silversmelter7' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#bob", "round : 1; phase : 2",
								"role : PRODUCER"))
						.and(userPlays("#bob",
								"round : 1; phase : 2; play : 1",
								"{ skip : true }"))
						.and(userPlays("#alice",
								"round : 1; phase : 2; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#alice", "round : 1; phase : 3",
								"role : PROSPECTOR"))
						.and(userPlays("#alice",
								"round : 1; phase : 3; play : 1", "{ }"))
						.and(userPlays("#bob",
								"round : 1; phase : 3; play : 2",
								"{ skip : true }"))
						.and(roleChosenBy("#bob", "round : 2; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#bob",
								"round : 2; phase : 1; play : 1",
								"{ skip : true }"))

				,

				when(userPlays("#alice", "round : 2; phase : 1; play : 2",
						"{ build : '#well', payment : [ '#prefecture' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 4 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 2 }")));
	}

}
