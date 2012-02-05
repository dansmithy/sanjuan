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
import com.github.dansmithy.driver.DeckOrder;
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
	public void testCannotChooseRoleWhenNotPlayerInGame() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						userExistsAndAuthenticated("#charlie")).and(
						copyGameIdBetweenUsers("#alice", "#charlie")),

				when(roleChosenBy("#charlie", "round : 1; phase : 1",
						"role : BUILDER")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED)).and(
						verifyResponseContains("{ code : 'NOT_YOUR_GAME' }")));
	}

	@Test
	public void testForcedToDiscardCardsIfHaveMoreThanSeven() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playRoundsOneAndTwoExceptFinalMove()),

				when(playRoundTwoFinalMove()),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1, handCount : 8 }, { 'name' : '#bob', victoryPoints: 1, handCount : 8 } ], 'roundNumber' : 3, 'currentRound' : { state : 'GOVERNOR', governorPhase : { currentStep : { numberOfCardsToDiscard : 1 } } } }")));
	}

	@Test
	public void testCannotPlayOnWhenAwaitingCardsToBeDiscarded() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playFullRoundsOneAndTwo()),

				when(roleChosenBy("#alice", "round : 3; phase : 1",
						"role : BUILDER")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'PHASE_NOT_ACTIVE' }")));

	}

	@Test
	public void testCanDiscardCardsSuccessfully() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playRoundsOneAndTwoExceptFinalMove()).and(
						playRoundTwoFinalMove()),

				when(userMakesGovernorPlay("#alice", "round : 3",
						"{ 'cardsToDiscard' : [ '#indigoplant3' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1, handCount: 7 }, { 'name' : '#bob', victoryPoints: 1, handCount : 8 } ], 'roundNumber' : 3, 'currentRound' : { state : 'GOVERNOR' } }")));

	}

	@Test
	public void testCanDiscardCardsAndChooseRoleAfterwards() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(playRoundsOneAndTwoExceptFinalMove())
						.and(playRoundTwoFinalMove())
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
						playRoundsOneAndTwoExceptFinalMove()).and(
						playRoundTwoFinalMove()),

				when(userMakesGovernorPlay("#bob", "round : 3",
						"{ 'cardsToDiscard' : [ '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED))
						.and(verifyResponseContains("{ code : 'NOT_CORRECT_USER' }")));

	}

	@Test
	public void testCannotDiscardCardsWhenNotPlayerInGame() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(playRoundsOneAndTwoExceptFinalMove())
						.and(playRoundTwoFinalMove())
						.and(userExistsAndAuthenticated("#charlie"))
						.and(copyGameIdBetweenUsers("#alice", "#charlie")),

				when(userMakesGovernorPlay("#charlie", "round : 3",
						"{ 'cardsToDiscard' : [ '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED)).and(
						verifyResponseContains("{ code : 'NOT_YOUR_GAME' }")));

	}

	@Test
	public void testCannotMakeGovernorChoiceWhenStillGovernorPhaseButChoiceAlreadyMade() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(playRoundsOneAndTwoExceptFinalMove())
						.and(playRoundTwoFinalMove())
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
						playRoundsOneAndTwoExceptFinalMove()).and(
						playRoundTwoFinalMove()),

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
						playRoundsOneAndTwoExceptFinalMove()).and(
						playRoundTwoFinalMove()),

				when(userMakesGovernorPlay("#alice", "round : 3",
						"{ 'cardsToDiscard' : [ ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(
						verifyResponseContains("{ code : 'UNDER_DISCARD' }")));
	}

	@Test
	public void testCannotDiscardCardsDoNotOwn() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playRoundsOneAndTwoExceptFinalMove()).and(
						playRoundTwoFinalMove()),

				when(userMakesGovernorPlay("#alice", "round : 3",
						"{ 'cardsToDiscard' : [ '#sugarmill3' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'NOT_OWNED_HAND_CARD' }")));
	}

	@Test
	public void testCannotDiscardWhenSpecifyIncorrectRound() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playRoundsOneAndTwoExceptFinalMove()).and(
						playRoundTwoFinalMove()),

				when(userMakesGovernorPlay("#alice", "round : 2",
						"{ 'cardsToDiscard' : [ '#indigoplant3' ] }")),

				then(verifyResponseCodeIs(HTTP_CONFLICT))
						.and(verifyResponseContains("{ code : 'PHASE_NOT_ACTIVE' }")));

	}

	@Test
	public void testCanDiscardMultipleCardsSuccessfully() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playFullRoundsOneAndTwo()).and(playFullRound3()),

				when(userMakesGovernorPlay("#bob", "round : 4",
						"{ 'cardsToDiscard' : [ '#indigoplant8' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1, handCount: 9 }, { 'name' : '#bob', victoryPoints: 1, handCount : 7 } ], 'roundNumber' : 4, 'currentRound' : { state : 'GOVERNOR' } }")));
	}

	@Test
	public void testCannotSpecifySameCardTwiceToDiscard() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playFullRoundsOneAndTwo()).and(playFullRound3()),

				when(userMakesGovernorPlay("#bob", "round : 4",
						"{ 'cardsToDiscard' : [ '#indigoplant7', '#indigoplant7' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'DUPLICATE_CHOICE' }")));
	}

	@Test
	public void testTowerOwnerCanHoldMoreThanSevenCards() {

		GameDriver context = ((SkeletonBddTestRunner<GameDriver>) bdd)
				.createContext();
		given(gameBegunWithTwoPlayers("#alice", "#bob", DeckOrder.Order4))
				.and(roleChosenBy("#alice", "round : 1; phase : 1", "role : BUILDER"))
				.and(userPlays("#alice", "round : 1; phase : 1; play : 1", "{  build : '#coffeeroaster', payment : [ '#prefecture', '#indigoplant3', '#indigoplant4' ]  }"))
				.and(userPlays("#bob", "round : 1; phase : 1; play : 2", "{  skip : true  }"))
				.and(roleChosenBy("#bob", "round : 1; phase : 2", "role : COUNCILLOR"))
				.and(userPlays("#bob", "round : 1; phase : 2; play : 1", "{  councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ]  }"))
				.and(userPlays("#alice", "round : 1; phase : 2; play : 2", "{  councilDiscarded : [ '#sugarmill3' ]  }"))
				.and(roleChosenBy("#alice", "round : 1; phase : 3", "role : PRODUCER"))
				.and(userPlays("#alice", "round : 1; phase : 3; play : 1", "{  productionFactories : [ '#indigoplant', '#coffeeroaster' ]  }"))
				.and(userPlays("#bob", "round : 1; phase : 3; play : 2", "{  productionFactories : [ '#indigoplant2' ]  }"))
				.and(roleChosenBy("#bob", "round : 2; phase : 1", "role : COUNCILLOR"))
				.and(userPlays("#bob", "round : 2; phase : 1; play : 1", "{  councilDiscarded : [ '#tobaccostorage', '#tobaccostorage2', '#tobaccostorage3', '#tobaccostorage4' ]  }"))
				.and(userPlays("#alice", "round : 2; phase : 1; play : 2", "{  councilDiscarded : [ '#tobaccostorage5' ]  }"))
				.and(roleChosenBy("#alice", "round : 2; phase : 2", "role : TRADER"))
				.and(userPlays("#alice", "round : 2; phase : 2; play : 1", "{  productionFactories : [ '#indigoplant', '#coffeeroaster' ]  }"))
				.and(userPlays("#bob", "round : 2; phase : 2; play : 2", "{  productionFactories : [ '#indigoplant2' ]  }"))
				.and(roleChosenBy("#bob", "round : 2; phase : 3", "role : BUILDER"))
				.and(userPlays("#bob", "round : 2; phase : 3; play : 1", "{  build : '#silversmelter7', payment : [ '#smithy', '#indigoplant6', '#indigoplant7', '#indigoplant8' ]  }"))
				.and(userPlays("#alice", "round : 2; phase : 3; play : 2", "{  build : '#tower', payment : [ '#sugarmill4', '#tobaccostorage7', '#tobaccostorage8' ]  }"))
				.and(roleChosenBy("#alice", "round : 3; phase : 1", "role : PRODUCER"))
				.and(userPlays("#alice", "round : 3; phase : 1; play : 1", "{  productionFactories : [ '#indigoplant', '#coffeeroaster' ]  }"))
				.and(userPlays("#bob", "round : 3; phase : 1; play : 2", "{  productionFactories : [ '#silversmelter7' ]  }"))
				.and(roleChosenBy("#bob", "round : 3; phase : 2", "role : COUNCILLOR"))
				.and(userPlays("#bob", "round : 3; phase : 2; play : 1", "{  councilDiscarded : [ '#coffeeroaster7', '#tradingpost', '#coffeeroaster8', '#silversmelter' ]  }"))
				.and(userPlays("#alice", "round : 3; phase : 2; play : 2", "{  councilDiscarded : [ '#silversmelter2' ]  }"))
				.and(roleChosenBy("#alice", "round : 3; phase : 3", "role : TRADER"))
				.and(userPlays("#alice", "round : 3; phase : 3; play : 1", "{  productionFactories : [ '#indigoplant', '#coffeeroaster' ]  }"))
				.and(userPlays("#bob", "round : 3; phase : 3; play : 2", "{  productionFactories : [ '#silversmelter7' ]  }"))
				.and(roleChosenBy("#bob", "round : 4; phase : 1", "role : PRODUCER"))
				.and(userPlays("#bob", "round : 4; phase : 1; play : 1", "{  productionFactories : [ '#silversmelter7', '#indigoplant2' ]  }"))
				.and(userPlays("#alice", "round : 4; phase : 1; play : 2", "{  productionFactories : [ '#coffeeroaster' ]  }"))
				.and(roleChosenBy("#alice", "round : 4; phase : 2", "role : TRADER"))
				.and(userPlays("#alice", "round : 4; phase : 2; play : 1", "{  productionFactories : [ '#coffeeroaster' ]  }"))
				.and(userPlays("#bob", "round : 4; phase : 2; play : 2", "{  skip : true  }"))
				.and(roleChosenBy("#bob", "round : 4; phase : 3", "role : COUNCILLOR"))
				.and(userPlays("#bob", "round : 4; phase : 3; play : 1", "{  councilDiscarded : [ '#triumphalarch', '#quarry2', '#archive', '#quarry3' ]  }"))
				.execute(context);

		when(userPlays("#alice", "round : 4; phase : 3; play : 2", "{  councilDiscarded : [ '#tradingpost2' ]  }")).execute(context);
		
		then(
				verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 5, handCount: 10 } ], 'currentRound' : { state : 'GOVERNOR', governorPhase : { currentStep : { playerName : '#bob' } } } }"))
				.execute(context);

	}


	@Test
	public void testChapelOwnerHasChapelOwnerSetToTrue() {
		bdd.runTest(

				given(playRoundOneAndBuildChapel()),

				when(getGameFor("#alice")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3, handCount: 3 } ], 'currentRound' : { state : 'GOVERNOR', governorPhase : { currentStep : { chapelOwner : true } } } }")));
	}

	@Test
	public void testChapelOwnerCanAddCardToChapel() {
		
		bdd.runTest(

				given(playRoundOneAndBuildChapel()),

				when(userMakesGovernorPlay("#alice", "round : 2",
						"{ 'chapelCard' : '#quarry'  }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 4, handCount: 2 } ], 'currentRound' : { state : 'PLAYING' } }")));
	}

	@Test
	public void testChapelOwnerCanChooseNotToAddToChapel() {

		bdd.runTest(

				given(playRoundOneAndBuildChapel()),

				when(userMakesGovernorPlay("#alice", "round : 2", "{ }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3, handCount: 3 } ], 'currentRound' : { state : 'PLAYING' } }")));
	}

	@Test
	public void testChapelOwnerCannotAddCardDoNotOwnToChapel() {
		bdd.runTest(

				given(playRoundOneAndBuildChapel()),

				when(userMakesGovernorPlay("#alice", "round : 2",
						"{ 'chapelCard' : '#sugarmill'  }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'NOT_OWNED_HAND_CARD' }")));
	}

	@Test
	public void testCanDiscardCardsPlusAddCardToChapel() {
		bdd.runTest(

				given(playRoundOneAndBuildChapel())
						.and(userMakesGovernorPlay("#alice", "round : 2", "{ }"))
						.and(playRoundTwoExceptFinalMove())
						.and(playRoundTwoFinalMove())
						.and(roundThreeGovernorPhaseWithChapel())
						.and(playRoundThree()).and(playRoundFour()),

				when(userMakesGovernorPlay("#alice", "round : 5",
						"{ 'cardsToDiscard' : [ '#quarry' ], chapelCard : '#carpenter' }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 4, handCount: 7 } ], 'currentRound' : { state : 'GOVERNOR' } }")));

	}

	@Test
	public void testCanDiscardTwoCardsInsteadOfAddingCardToChapel() {
		bdd.runTest(

				given(playRoundOneAndBuildChapel())
						.and(userMakesGovernorPlay("#alice", "round : 2", "{ }"))
						.and(playRoundTwoExceptFinalMove())
						.and(playRoundTwoFinalMove())
						.and(roundThreeGovernorPhaseWithChapel())
						.and(playRoundThree()).and(playRoundFour()),

				when(userMakesGovernorPlay("#alice", "round : 5",
						"{ 'cardsToDiscard' : [ '#quarry', '#carpenter' ] }")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 3, handCount: 7 } ], 'currentRound' : { state : 'GOVERNOR' } }")));

	}

	@Test
	public void testMustDiscardFullAmountIfDoNotAddCardToChapel() {
		bdd.runTest(

				given(playRoundOneAndBuildChapel())
						.and(userMakesGovernorPlay("#alice", "round : 2", "{ }"))
						.and(playRoundTwoExceptFinalMove())
						.and(playRoundTwoFinalMove())
						.and(roundThreeGovernorPhaseWithChapel())
						.and(playRoundThree()).and(playRoundFour()),

				when(userMakesGovernorPlay("#alice", "round : 5",
						"{ 'cardsToDiscard' : [ '#quarry' ] }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST)).and(
						verifyResponseContains("{ code : 'UNDER_DISCARD' }")));

	}

	@Test
	public void testCannotSpecifyChapelCardIfNotChapelOwner() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						playRoundsOneAndTwoExceptFinalMove()).and(
						playRoundTwoFinalMove()),

				when(userMakesGovernorPlay("#alice", "round : 3",
						"{ 'chapelCard' : '#indigoplant3' }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'NOT_OWNED_BUILDING' }")));

	}

	private BddPart<GameDriver> playFullRoundsOneAndTwo() {
		return new GivenBddParts(playRoundsOneAndTwoExceptFinalMove())
				.and(playRoundTwoFinalMove());
	}

	private BddPart<GameDriver> playRoundsOneAndTwoExceptFinalMove() {
		return new GivenBddParts(playRoundOne())
				.and(playRoundTwoExceptFinalMove());
	}

	private BddPart<GameDriver> playFullRound3() {
		return new GivenBddParts(roundThreeGovernorPhase())
				.and(playRoundThree());
	}

	/**
	 * At end of this alice has 7 cards and bob has 6 cards.
	 */
	private BddPart<GameDriver> playRoundOne() {
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

		;
	}

	/**
	 * At end of this alice has 8 cards and bob has 8 cards.
	 */
	private BddPart<GameDriver> playRoundTwoExceptFinalMove() {
		return new GivenBddParts(roleChosenBy("#bob", "round : 2; phase : 1",
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

	private BddPart<GameDriver> playRoundTwoFinalMove() {
		return userPlays("#alice", "round : 2; phase : 3; play : 2",
				"{ skip : true }");
	}

	/**
	 * Both players discard one card to bring down to 7
	 */
	private BddPart<GameDriver> roundThreeGovernorPhase() {
		return new GivenBddParts(userMakesGovernorPlay("#alice", "round : 3",
				"{ 'cardsToDiscard' : [ '#indigoplant3' ] }"))
				.and(userMakesGovernorPlay("#bob", "round : 3",
						"{ 'cardsToDiscard' : [ '#indigoplant7' ] }"));
	}

	/**
	 * At end of round alice has 9 cards and bob has 8 cards.
	 * 
	 * If used with chapel, then alice has 6 cards and bob has 8 cards
	 */
	private BddPart<GameDriver> playRoundThree() {
		return new GivenBddParts(roleChosenBy("#alice", "round : 3; phase : 1",
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
						"{ skip : true }"));
	}

	/**
	 * Round 1 where alice builds chapel.
	 * 
	 * At end of round, alice has 4 cards and bob has 8 cards
	 */
	private BddPart<GameDriver> playRoundOneAndBuildChapel() {
		return new GivenBddParts(gameBegunWithTwoPlayers("#alice", "#bob",
				DeckOrder.Order3))
				.and(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : BUILDER"))
				.and(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ build : '#chapel', payment : [ '#indigoplant3', '#indigoplant4' ] }"))
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
						"{ productionFactories : [ '#indigoplant2' ] }"));
	}

	/**
	 * After bob discarding one, alice has 4 cards and bob has 7 cards
	 */
	private BddPart<GameDriver> roundThreeGovernorPhaseWithChapel() {
		return new GivenBddParts(userMakesGovernorPlay("#alice", "round : 3",
				"{ }")).and(userMakesGovernorPlay("#bob", "round : 3",
				"{ 'cardsToDiscard' : [ '#indigoplant7' ] }"));
	}

	/**
	 * At end of round alice has 8 cards and bob has 9 cards (when alice has
	 * chapel)
	 */
	private BddPart<GameDriver> playRoundFour() {
		return new GivenBddParts(userMakesGovernorPlay("#bob", "round : 4",
				"{ 'cardsToDiscard' : [ '#tobaccostorage4' ] }"))
				.and(userMakesGovernorPlay("#alice", "round : 4", "{ }"))
				.and(roleChosenBy("#bob", "round : 4; phase : 1",
						"role : TRADER"))
				.and(userPlays("#bob", "round : 4; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant2' ] }"))
				.and(userPlays("#alice", "round : 4; phase : 1; play : 2",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(roleChosenBy("#alice", "round : 4; phase : 2",
						"role : PROSPECTOR"))
				.and(userPlays("#alice", "round : 4; phase : 2; play : 1",
						"{ }"))
				.and(userPlays("#bob", "round : 4; phase : 2; play : 2",
						"{ skip : true }"))
				.and(roleChosenBy("#bob", "round : 4; phase : 3",
						"role : COUNCILLOR"))
				.and(userPlays(
						"#bob",
						"round : 4; phase : 3; play : 1",
						"{ councilDiscarded : [ '#coffeeroaster5', '#coffeeroaster6', '#coffeeroaster7', '#coffeeroaster8' ] }"))
				.and(userPlays("#alice", "round : 4; phase : 3; play : 2",
						"{ councilDiscarded : [ '#silversmelter' ] }"))

		;
	}
}
