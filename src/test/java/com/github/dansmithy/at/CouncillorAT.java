package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.Test;

public class CouncillorAT extends BaseAT {

	@Test
	public void testCorrectNumberOfCardsOffered() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(anyNumberOfTwitterMessagesPermitted()),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : COUNCILLOR")),

				then(verifySuccessfulResponseContains("{ 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING', currentPlay : { 'state' : 'AWAITING_INPUT', 'offered' : { 'councilOffered' : [ '#well', '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ], 'councilRetainCount' : 1, 'councilCanDiscardHandCards' : false } } } } }")));
	}

	@Test
	public void testCanCouncilCards() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(anyNumberOfTwitterMessagesPermitted()).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING' } } }")));
		
		//plays^state : [ { 'state' : 'COMPLETED', 'playChoice' : { 'councilDiscarded' : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] } } ] } ] 
	}

	@Test
	public void testCannotDiscardCardDoNotOwn() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(anyNumberOfTwitterMessagesPermitted()).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#silversmelter' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(verifyResponseContains("{ code : 'NOT_OWNED_COUNCIL_DISCARD' }")));
	}
	
	@Test
	public void testCannotDiscardTooFewCards() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(anyNumberOfTwitterMessagesPermitted()).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill'] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(verifyResponseContains("{ code : 'UNDER_DISCARD' }")));
	}
	
	@Test
	public void testCannotDiscardTooManyCards() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(anyNumberOfTwitterMessagesPermitted()).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [  '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2', '#well' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(verifyResponseContains("{ code : 'OVER_DISCARD' }")));
	}		
	
	@Test
	public void testCannotCheatAndDiscardSameCardTwice() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(anyNumberOfTwitterMessagesPermitted()).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [  '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(verifyResponseContains("{ code : 'DUPLICATE_CHOICE' }")));
	}		
	
	@Test
	public void testCannotDiscardHandCardsWithoutArchive() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(anyNumberOfTwitterMessagesPermitted()).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : COUNCILLOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(verifyResponseContains("{ code : 'NOT_OWNED_COUNCIL_DISCARD' }")));
	}

}
