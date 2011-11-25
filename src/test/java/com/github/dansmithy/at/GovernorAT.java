package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.Test;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.bdd.GivenBddParts;
import com.github.dansmithy.bdd.SkeletonBddTestRunner;
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

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT' } ] } ] } ] }")));
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
	public void testForcedToDiscardCardsIfHaveMoreThanSeven() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						accrueOverSevenCards()),

				when(initiateGovernorPhase()),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 3, 'rounds^state' : [ { state : 'GOVERNOR', governorPhase : { governorSteps : [ { cardsToDiscard : 2 } ] } } ] }")));

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

	private BddPart<GameDriver> playUpToGovernorDiscardCardPhase() {
		return new GivenBddParts(accrueOverSevenCards())
				.and(initiateGovernorPhase());
	}

	private BddPart<GameDriver> initiateGovernorPhase() {
		return userPlays("#alice", "round : 2; phase : 3; play : 2",
				"{ productionFactories : [ '#indigoplant' ] }");
	}

	private BddPart<GameDriver> accrueOverSevenCards() {
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
						"role : PROSPECTOR"))
				.and(userPlays("#alice", "round : 2; phase : 2; play : 1",
						"{ }"))
				.and(userPlays("#bob", "round : 2; phase : 2; play : 2",
						"{ skip : true }"))
				.and(roleChosenBy("#bob", "round : 2; phase : 3",
						"role : PRODUCER"))
				.and(userPlays("#bob", "round : 2; phase : 3; play : 1",
						"{ productionFactories : [ '#indigoplant2' ] }"))

		;
	}
}
