package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.Ignore;
import org.junit.Test;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.bdd.GivenBddParts;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class GovernorAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	@Test
	public void testCanChooseRole() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : BUILDER")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING', currentPlay : { 'state' : 'AWAITING_INPUT' } } } }")));
	}

	@Test
	public void testCannotChooseRoleWhenNotYourTurn() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(roleChosenBy("#bob", "round : 1; phase : 1",
						"role : BUILDER")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED))
						.and(verifyResponseContains("{ code : 'NOT_CORRECT_USER' }")));
	}

	@Test
	public void testCannotChooseRoleWhenNotRoleChoiceTime() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : PROSPECTOR")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'PHASE_NOT_ACTIVE' }")));
	}

	@Test
	public void testCannotChooseAlreadyChosenRole() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(userPlays("#alice",
								"round : 1; phase : 1; play : 1",
								"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }"))
						.and(userPlays("#bob",
								"round : 1; phase : 1; play : 2",
								"{ build : '#smithy', payment : [ '#indigoplant6' ] }")),

				when(roleChosenBy("#bob", "round : 1; phase : 2",
						"role : BUILDER")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'ROLE_ALREADY_TAKEN' }")));
	}
	
	@Test
	public void testCannotChooseRoleWhenNotAPlayerInTheGame() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(userExistsAndAuthenticated("#charlie")).and(copyGameIdBetweenUsers("#alice", "#charlie")),

				when(roleChosenBy("#charlie", "round : 1; phase : 1",
						"role : BUILDER")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED)).and(verifyResponseContains("{ code : 'NOT_YOUR_GAME' }")));
	}
	
	@Test
	public void testForcedToDiscardCardsIfHaveMoreThanSeven() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						bothPlayersAccrueEightCards()),

				when(initiateGovernorPhase()),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1, handCount : 8 }, { 'name' : '#bob', victoryPoints: 1, handCount : 8 } ], 'roundNumber' : 3, 'currentRound' : { state : 'GOVERNOR', governorPhase : { currentStep : { numberOfCardsToDiscard : 1 } } } }")));
	}

	@Test
	public void testCannotPlayOnWhenAwaitingCardsToBeDiscarded() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playUpToGovernorDiscardCardPhase()),

				when(roleChosenBy("#alice", "round : 3; phase : 1",
						"role : BUILDER")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'PHASE_NOT_ACTIVE' }")));

	}

	@Test
	public void testCanDiscardCardsSuccessfully() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						bothPlayersAccrueEightCards()).and(
						initiateGovernorPhase()),

				when(userMakesGovernorPlay("#alice", "round : 3",
						"{ 'cardsToDiscard' : [ '#indigoplant3' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1, handCount: 7 }, { 'name' : '#bob', victoryPoints: 1, handCount : 8 } ], 'roundNumber' : 3, 'currentRound' : { state : 'GOVERNOR' } }")));

	}

	@Test
	public void testCanDiscardCardsAndChooseRoleAfterwards() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(bothPlayersAccrueEightCards())
						.and(initiateGovernorPhase())
						.and(userMakesGovernorPlay("#alice", "round : 3",
								"{ 'cardsToDiscard' : [ '#indigoplant3' ] }"))
						.and(userMakesGovernorPlay("#bob", "round : 3",
								"{ 'cardsToDiscard' : [ '#indigoplant7' ] }")),

				when(roleChosenBy("#alice", "round : 3; phase : 1",
						"role : BUILDER")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1, handCount: 7 }, { 'name' : '#bob', victoryPoints: 1, handCount : 7 } ], 'roundNumber' : 3, 'currentRound' : { state : 'PLAYING' } }")));

	}

	@Test
	public void testCannotMakeGovernorChoiceWhenNotGovernorPhase() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(userMakesGovernorPlay("#alice", "round : 1",
						"{ 'cardsToDiscard' : [ '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'PHASE_NOT_ACTIVE' }")));

	}

	@Test
	public void testCannotDiscardCardsWhenNotCurrentPlayer() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						bothPlayersAccrueEightCards()).and(
						initiateGovernorPhase()),

				when(userMakesGovernorPlay("#bob", "round : 3",
						"{ 'cardsToDiscard' : [ '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED))
						.and(verifyResponseContains("{ code : 'NOT_CORRECT_USER' }")));

	}

	@Test
	public void testCannotMakeGovernorChoiceWhenStillGovernorPhaseButChoiceAlreadyMade() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(bothPlayersAccrueEightCards())
						.and(initiateGovernorPhase())
						.and(userMakesGovernorPlay("#alice", "round : 3",
								"{ 'cardsToDiscard' : [ '#indigoplant3' ] }")),

				when(userMakesGovernorPlay("#alice", "round : 3",
						"{ 'cardsToDiscard' : [ '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED))
						.and(verifyResponseContains("{ code : 'NOT_CORRECT_USER' }")));
	}

	@Test
	public void testCannotDiscardTooManyCards() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						bothPlayersAccrueEightCards()).and(
						initiateGovernorPhase()),

				when(userMakesGovernorPlay(
						"#alice",
						"round : 3",
						"{ 'cardsToDiscard' : [ '#indigoplant3', '#indigoplant4', '#tobaccostorage' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(
						verifyResponseContains("{ code : 'OVER_DISCARD' }")));
	}

	@Test
	public void testCannotDiscardTooFewCards() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						bothPlayersAccrueEightCards()).and(
						initiateGovernorPhase()),

				when(userMakesGovernorPlay("#alice", "round : 3",
						"{ 'cardsToDiscard' : [ ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(
						verifyResponseContains("{ code : 'UNDER_DISCARD' }")));
	}

	@Test
	public void testCannotDiscardCardsDoNotOwn() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						bothPlayersAccrueEightCards()).and(
						initiateGovernorPhase()),

				when(userMakesGovernorPlay("#alice", "round : 3",
						"{ 'cardsToDiscard' : [ '#sugarmill3' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'NOT_OWNED_HAND_CARD' }")));
	}

	@Test
	public void testCannotDiscardWhenSpecifyIncorrectRound() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						bothPlayersAccrueEightCards()).and(
						initiateGovernorPhase()),

				when(userMakesGovernorPlay("#alice", "round : 2",
						"{ 'cardsToDiscard' : [ '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'PHASE_NOT_ACTIVE' }")));

	}

	@Test
	public void testCanDiscardMultipleCardsSuccessfully() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playUpToGovernorDiscardCardPhase()).and(
						discardThenAccrueOverEightCards()),

				when(userMakesGovernorPlay("#bob", "round : 4",
						"{ 'cardsToDiscard' : [ '#indigoplant8' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1, handCount: 9 }, { 'name' : '#bob', victoryPoints: 1, handCount : 7 } ], 'roundNumber' : 4, 'currentRound' : { state : 'GOVERNOR' } }")));
	}

	@Test
	public void testCannotSpecifySameCardTwiceToDiscard() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playUpToGovernorDiscardCardPhase()).and(
						discardThenAccrueOverEightCards()),

				when(userMakesGovernorPlay("#bob", "round : 4",
						"{ 'cardsToDiscard' : [ '#indigoplant7', '#indigoplant7' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'DUPLICATE_CHOICE' }")));
	}

	private BddPart<GameDriver> playUpToGovernorDiscardCardPhase() {
		return new GivenBddParts(bothPlayersAccrueEightCards())
				.and(initiateGovernorPhase());
	}

	private BddPart<GameDriver> discardThenAccrueOverEightCards() {
		return new GivenBddParts(userMakesGovernorPlay("#alice", "round : 3",
				"{ 'cardsToDiscard' : [ '#indigoplant3' ] }"))
				.and(userMakesGovernorPlay("#bob", "round : 3",
						"{ 'cardsToDiscard' : [ '#indigoplant7' ] }"))
				.and(roleChosenBy("#alice", "round : 3; phase : 1",
						"role : TRADER"))
				.and(userPlays("#alice", "round : 3; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(userPlays("#bob", "round : 3; phase : 1; play : 2",
						"{ productionFactories : [ '#indigoplant2' ] }"))
				.and(roleChosenBy("#bob", "round : 3; phase : 2",
						"role : PRODUCER"))
				.and(userPlays("#bob", "round : 3; phase : 2; play : 1",
						"{ productionFactories : [ '#indigoplant2' ] }"))
				.and(userPlays("#alice", "round : 3; phase : 2; play : 2",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(roleChosenBy("#alice", "round : 3; phase : 3",
						"role : PROSPECTOR"))
				.and(userPlays("#alice", "round : 3; phase : 3; play : 1",
						"{  }"))
				.and(userPlays("#bob", "round : 3; phase : 3; play : 2",
						"{ skip : true }"))

		;
	}

	private BddPart<GameDriver> initiateGovernorPhase() {
		return userPlays("#alice", "round : 2; phase : 3; play : 2",
				"{ skip : true }");
	}

	private BddPart<GameDriver> bothPlayersAccrueEightCards() {
		return new GivenBddParts(roleChosenBy("#alice", "round : 1; phase : 1",
				"role : PROSPECTOR"))
				.and(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ }"))
				.and(userPlays("#bob", "round : 1; phase : 1; play : 2",
						"{ skip : true }"))
				.and(roleChosenBy("#bob", "round : 1; phase : 2",
						"role : COUNCILLOR"))
				.and(userPlays(
						"#bob",
						"round : 1; phase : 2; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] }"))
				.and(userPlays("#alice", "round : 1; phase : 2; play : 2",
						"{ councilDiscarded : [ '#sugarmill4' ] }"))
				.and(roleChosenBy("#alice", "round : 1; phase : 3",
						"role : PRODUCER"))
				.and(userPlays("#alice", "round : 1; phase : 3; play : 1",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(userPlays("#bob", "round : 1; phase : 3; play : 2",
						"{ productionFactories : [ '#indigoplant2' ] }"))
				.and(roleChosenBy("#bob", "round : 2; phase : 1",
						"role : TRADER"))
				.and(userPlays("#bob", "round : 2; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant2' ] }"))
				.and(userPlays("#alice", "round : 2; phase : 1; play : 2",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(roleChosenBy("#alice", "round : 2; phase : 2",
						"role : PRODUCER"))
				.and(userPlays("#alice", "round : 2; phase : 2; play : 1",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(userPlays("#bob", "round : 2; phase : 2; play : 2",
						"{ productionFactories : [ '#indigoplant2' ] }"))
				.and(roleChosenBy("#bob", "round : 2; phase : 3",
						"role : PROSPECTOR"))
				.and(userPlays("#bob", "round : 2; phase : 3; play : 1", "{  }"))

		;
	}
}
