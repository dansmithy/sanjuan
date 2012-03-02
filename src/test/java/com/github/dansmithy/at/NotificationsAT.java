package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.Test;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.GivenBddParts;
import com.github.dansmithy.driver.GameDriver;

public class NotificationsAT extends BaseAT {

    @Test
    public void testLoginAddsUserToDatabase() {

        bdd.runTest(

                given(userAuthenticated("#alice")),

                when(getUser("#alice")),

                then(verifySuccessfulResponseContains("{ 'username' : '#alice', 'timesLoggedIn' : 1 }")));

    }

    @Test
    public void testLoginTwiceGetsRecorded() {

        bdd.runTest(

                given(userAuthenticated("#alice")).and(userAuthenticated("#alice")),

                when(getUser("#alice")),

                then(verifySuccessfulResponseContains("{ 'username' : '#alice', 'timesLoggedIn' : 2 }")));

    }

	@Test
	public void testSendMessageAfterMove() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(roleChosenBy("#alice", "round : 1; phase : 1",
								"role : BUILDER"))
						.and(twitterRespondsToMessageFor("#bob")),

				when(userPlays("#alice",
						"round : 1; phase : 1; play : 1",
						"{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }")),

				then(verifyResponseCodeIs(HTTP_OK)));
	}
	
	@Test
	public void testSendAllRequiredMessagesInTwoRoundsOfPlay() {

		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
					.and(playRoundOne()),

				when(playRoundTwo()),

				then(verifyResponseCodeIs(HTTP_OK)));
	}	
	
	@Test
	public void testSendMessageForGovernorMove() {
		bdd.runTest(

				given(gameBegunWithTwoPlayers("#alice", "#bob"))
						.and(playRoundOne())
						.and(playRoundTwo())
						.and(twitterRespondsToMessageFor("#bob"))
						.and(userMakesGovernorPlay("#alice", "round : 3",
								"{ 'cardsToDiscard' : [ '#indigoplant3' ] }"))
						.and(twitterRespondsToMessageFor("#alice")),
								
				when(userMakesGovernorPlay("#bob", "round : 3",
						"{ 'cardsToDiscard' : [ '#indigoplant7' ] }")),
										
				then(verifyResponseCodeIs(HTTP_OK)));
	}

    @Test
    public void testSendCompletionMessagesToLoser() {
        bdd.runTest(

                given(gameBegunWithTwoPlayers("#alice", "#bob"))
                        .and(gameAlmostCompleted())
                        .and(twitterRespondsToMessageFor("#bob")),

                when(finalWinningMove()),

                then(verifyResponseCodeIs(HTTP_OK)));
    }
	
	/**
	 * At end of this alice has 7 cards and bob has 6 cards.
	 */
	private static BddPart<GameDriver> playRoundOne() {
		return new GivenBddParts(roleChosenBy("#alice", "round : 1; phase : 1",
				"role : PROSPECTOR"))
				.and(twitterRespondsToMessageFor("#bob"))
				.and(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{ }"))
				.and(userPlays("#bob", "round : 1; phase : 1; play : 2",
						"{ skip : true }"))
				.and(roleChosenBy("#bob", "round : 1; phase : 2",
						"role : COUNCILLOR"))
				.and(twitterRespondsToMessageFor("#alice"))						
				.and(userPlays(
						"#bob",
						"round : 1; phase : 2; play : 1",
						"{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] }"))
				.and(userPlays("#alice", "round : 1; phase : 2; play : 2",
						"{ councilDiscarded : [ '#sugarmill4' ] }"))
				.and(roleChosenBy("#alice", "round : 1; phase : 3",
						"role : PRODUCER"))
				.and(twitterRespondsToMessageFor("#bob"))
				.and(userPlays("#alice", "round : 1; phase : 3; play : 1",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(userPlays("#bob", "round : 1; phase : 3; play : 2",
						"{ productionFactories : [ '#indigoplant2' ] }"))

		;
	}	
	
	/**
	 * At end of this alice has 8 cards and bob has 8 cards.
	 */
	private static BddPart<GameDriver> playRoundTwo() {
		return new GivenBddParts(roleChosenBy("#bob", "round : 2; phase : 1",
				"role : TRADER"))
				.and(twitterRespondsToMessageFor("#alice"))
				.and(userPlays("#bob", "round : 2; phase : 1; play : 1",
						"{ productionFactories : [ '#indigoplant2' ] }"))
				.and(userPlays("#alice", "round : 2; phase : 1; play : 2",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(roleChosenBy("#alice", "round : 2; phase : 2",
						"role : PRODUCER"))
				.and(twitterRespondsToMessageFor("#bob"))						
				.and(userPlays("#alice", "round : 2; phase : 2; play : 1",
						"{ productionFactories : [ '#indigoplant' ] }"))
				.and(userPlays("#bob", "round : 2; phase : 2; play : 2",
						"{ productionFactories : [ '#indigoplant2' ] }"))
				.and(roleChosenBy("#bob", "round : 2; phase : 3",
						"role : PROSPECTOR"))
				.and(twitterRespondsToMessageFor("#alice"))						
				.and(userPlays("#bob", "round : 2; phase : 3; play : 1", "{  }"))
				.and(userPlays("#alice", "round : 2; phase : 3; play : 2",
				"{ skip : true }"))

		;
	}

    private BddPart<GameDriver> finalWinningMove() {
        return userPlays("#alice", "round : 12; phase : 1; play : 2",
                "{ build : '#indigoplant5', payment : [ '#palace2' ] }");
    }



    private BddPart<GameDriver> gameAlmostCompleted() {
        return new GivenBddParts(
                 roleChosenBy("#alice", "round : 1; phase : 1", "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 1; phase : 1; play : 1",
                        "{ build : '#prefecture', payment : [ '#indigoplant3', '#indigoplant4' ] }"))
                .and(userPlays("#bob", "round : 1; phase : 1; play : 2",
                        "{ build : '#smithy', payment : [ '#indigoplant6' ] }"))
                .and(roleChosenBy("#bob", "round : 1; phase : 2",
                        "role : COUNCILLOR"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays(
                        "#bob",
                        "round : 1; phase : 2; play : 1",
                        "{ councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2' ] }"))
                .and(userPlays("#alice", "round : 1; phase : 2; play : 2",
                        "{ councilDiscarded : [ ] }"))
                .and(roleChosenBy("#alice", "round : 1; phase : 3",
                        "role : PROSPECTOR"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 1; phase : 3; play : 1",
                        "{ }"))
                .and(userPlays("#bob", "round : 1; phase : 3; play : 2",
                        "{ 'skip' : true }"))

                .and(roleChosenBy("#bob", "round : 2; phase : 1",
                        "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 2; phase : 1; play : 1",
                        "{ build : '#coffeeroaster', payment : [ '#indigoplant7', '#indigoplant8' ] }"))
                .and(userPlays(
                        "#alice",
                        "round : 2; phase : 1; play : 2",
                        "{ build : '#quarry', payment : [ '#silversmelter7', '#sugarmill3', '#sugarmill4', '#sugarmill5' ] }"))
                .and(roleChosenBy("#alice", "round : 2; phase : 2",
                        "role : COUNCILLOR"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 2; phase : 2; play : 1",
                        "{ councilDiscarded : [ '#sugarmill7', '#sugarmill8', '#tobaccostorage2' ] }"))
                .and(userPlays("#bob", "round : 2; phase : 2; play : 2",
                        "{ councilDiscarded : [ '#tobaccostorage4' ] }"))
                .and(roleChosenBy("#bob", "round : 2; phase : 3",
                        "role : PRODUCER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 2; phase : 3; play : 1",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
                .and(userPlays("#alice", "round : 2; phase : 3; play : 2",
                        "{ productionFactories : [ '#indigoplant' ] }"))
                .and(roleChosenBy("#alice", "round : 3; phase : 1",
                        "role : TRADER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 3; phase : 1; play : 1",
                        "{ productionFactories : [ '#indigoplant' ] }"))
                .and(userPlays("#bob", "round : 3; phase : 1; play : 2",
                        "{ productionFactories : [ '#coffeeroaster' ] }"))
                .and(roleChosenBy("#bob", "round : 3; phase : 2",
                        "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 3; phase : 2; play : 1",
                        "{ build : '#well', payment : [ '#tobaccostorage3' ] }"))
                .and(userPlays(
                        "#alice",
                        "round : 3; phase : 2; play : 2",
                        "{ build : '#sugarmill6', payment : [ '#tobaccostorage', '#tobaccostorage8' ] }"))
                .and(roleChosenBy("#alice", "round : 3; phase : 3",
                        "role : PRODUCER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 3; phase : 3; play : 1",
                        "{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
                .and(userPlays("#bob", "round : 3; phase : 3; play : 2",
                        "{ productionFactories : [ '#coffeeroaster' ] }"))

                .and(roleChosenBy("#bob", "round : 4; phase : 1",
                        "role : TRADER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 4; phase : 1; play : 1",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
                .and(userPlays("#alice", "round : 4; phase : 1; play : 2",
                        "{ productionFactories : [ '#sugarmill6' ] }"))
                .and(roleChosenBy("#alice", "round : 4; phase : 2",
                        "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 4; phase : 2; play : 1",
                        "{ build : '#carpenter', payment : [ '#silversmelter' ] }"))
                .and(userPlays(
                        "#bob",
                        "round : 4; phase : 2; play : 2",
                        "{ build : '#aqueduct', payment : [ '#coffeeroaster2', '#coffeeroaster3', '#coffeeroaster7' ] }"))
                .and(roleChosenBy("#bob", "round : 4; phase : 3",
                        "role : PRODUCER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 4; phase : 3; play : 1",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
                .and(userPlays("#alice", "round : 4; phase : 3; play : 2",
                        "{ productionFactories : [ '#sugarmill6' ] }"))

                .and(roleChosenBy("#alice", "round : 5; phase : 1",
                        "role : TRADER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 5; phase : 1; play : 1",
                        "{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
                .and(userPlays("#bob", "round : 5; phase : 1; play : 2",
                        "{ productionFactories : [ '#coffeeroaster' ] }"))
                .and(roleChosenBy("#bob", "round : 5; phase : 2",
                        "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 5; phase : 2; play : 1",
                        "{ build : '#tradingpost', payment : [ '#aqueduct3' ] }"))
                .and(userPlays("#alice", "round : 5; phase : 2; play : 2",
                        "{ skip : true }"))
                .and(roleChosenBy("#alice", "round : 5; phase : 3",
                        "role : PRODUCER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 5; phase : 3; play : 1",
                        "{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
                .and(userPlays("#bob", "round : 5; phase : 3; play : 2",
                        "{ productionFactories : [ '#coffeeroaster' ] }"))

                .and(roleChosenBy("#bob", "round : 6; phase : 1",
                        "role : TRADER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 6; phase : 1; play : 1",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
                .and(userPlays("#alice", "round : 6; phase : 1; play : 2",
                        "{ productionFactories : [ '#indigoplant' ] }"))
                .and(roleChosenBy("#alice", "round : 6; phase : 2",
                        "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 6; phase : 2; play : 1",
                        "{ build : '#markethall', payment : [ '#aqueduct2', '#quarry2' ] }"))
                .and(userPlays("#bob", "round : 6; phase : 2; play : 2",
                        "{ build : '#marketstand', payment : [ '#carpenter2', '#carpenter3' ] }"))
                .and(roleChosenBy("#bob", "round : 6; phase : 3",
                        "role : PRODUCER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 6; phase : 3; play : 1",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
                .and(userPlays("#alice", "round : 6; phase : 3; play : 2",
                        "{ productionFactories : [ '#indigoplant' ] }"))

                .and(roleChosenBy("#alice", "round : 7; phase : 1",
                        "role : TRADER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 7; phase : 1; play : 1",
                        "{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
                .and(userPlays("#bob", "round : 7; phase : 1; play : 2",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
                .and(roleChosenBy("#bob", "round : 7; phase : 2",
                        "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays(
                        "#bob",
                        "round : 7; phase : 2; play : 1",
                        "{ build : '#triumphalarch', payment : [ '#coffeeroaster8', '#statue2', '#prefecture3', '#smithy3', '#well3' ] }"))
                .and(userPlays("#alice", "round : 7; phase : 2; play : 2",
                        "{ build : '#archive', payment : [] }"))
                .and(roleChosenBy("#alice", "round : 7; phase : 3",
                        "role : PRODUCER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 7; phase : 3; play : 1",
                        "{ productionFactories : [ '#indigoplant', '#sugarmill6' ] }"))
                .and(userPlays("#bob", "round : 7; phase : 3; play : 2",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))

                .and(roleChosenBy("#bob", "round : 8; phase : 1",
                        "role : TRADER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 8; phase : 1; play : 1",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
                .and(userPlays("#alice", "round : 8; phase : 1; play : 2",
                        "{ productionFactories : [ '#sugarmill6' ] }"))
                .and(roleChosenBy("#alice", "round : 8; phase : 2",
                        "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays(
                        "#alice",
                        "round : 8; phase : 2; play : 1",
                        "{ build : '#cityhall', payment : [ '#tradingpost3', '#well2', '#archive3', '#goldmine3' ] }"))
                .and(userPlays("#bob", "round : 8; phase : 2; play : 2",
                        "{ build : '#statue3', payment : [ '#chapel2', '#chapel3', '#archive2' ] }"))
                .and(roleChosenBy("#bob", "round : 8; phase : 3",
                        "role : PROSPECTOR"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 8; phase : 3; play : 1", "{ }"))
                .and(userPlays("#alice", "round : 8; phase : 3; play : 2",
                        "{ skip : true }"))

                .and(roleChosenBy("#alice", "round : 9; phase : 1",
                        "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 9; phase : 1; play : 1",
                        "{ build : '#poorhouse', payment : [ ] }"))
                .and(userPlays(
                        "#bob",
                        "round : 9; phase : 1; play : 2",
                        "{ build : '#victorycolumn', payment : [ '#goldmine2', '#blackmarket', '#crane2', '#crane3' ] }"))
                .and(roleChosenBy("#bob", "round : 9; phase : 2",
                        "role : PROSPECTOR"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 9; phase : 2; play : 1", "{ }"))
                .and(userPlays("#alice", "round : 9; phase : 2; play : 2",
                        "{ skip : true }"))
                .and(roleChosenBy("#alice", "round : 9; phase : 3",
                        "role : TRADER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 9; phase : 3; play : 1",
                        "{ productionFactories : [ '#indigoplant' ] }"))
                .and(userPlays("#bob", "round : 9; phase : 3; play : 2",
                        "{ skip : true }"))

                .and(roleChosenBy("#bob", "round : 10; phase : 1",
                        "role : PRODUCER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 10; phase : 1; play : 1",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
                .and(userPlays("#alice", "round : 10; phase : 1; play : 2",
                        "{ productionFactories : [ '#sugarmill6' ] }"))
                .and(roleChosenBy("#alice", "round : 10; phase : 2",
                        "role : TRADER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 10; phase : 2; play : 1",
                        "{ productionFactories : [ '#sugarmill6' ] }"))
                .and(userPlays("#bob", "round : 10; phase : 2; play : 2",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))
                .and(roleChosenBy("#bob", "round : 10; phase : 3",
                        "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#alice"))
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
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 11; phase : 1; play : 1",
                        "{ build : '#hero', payment : [ '#silversmelter8', '#hero3' ] }"))
                .and(userPlays("#bob", "round : 11; phase : 1; play : 2",
                        "{ skip : true }"))
                .and(roleChosenBy("#bob", "round : 11; phase : 2",
                        "role : PROSPECTOR"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 11; phase : 2; play : 1",
                        "{ }"))
                .and(userPlays("#alice", "round : 11; phase : 2; play : 2",
                        "{ skip : true }"))
                .and(roleChosenBy("#alice", "round : 11; phase : 3",
                        "role : PRODUCER"))
                .and(twitterRespondsToMessageFor("#bob"))
                .and(userPlays("#alice", "round : 11; phase : 3; play : 1",
                        "{ productionFactories : [ '#sugarmill6' ] }"))
                .and(userPlays("#bob", "round : 11; phase : 3; play : 2",
                        "{ productionFactories : [ '#indigoplant2', '#coffeeroaster' ] }"))

                .and(roleChosenBy("#bob", "round : 12; phase : 1",
                        "role : BUILDER"))
                .and(twitterRespondsToMessageFor("#alice"))
                .and(userPlays("#bob", "round : 12; phase : 1; play : 1",
                        "{ build : '#tobaccostorage6', payment : [ '#tower' ] }"))


                ;
    }
}
