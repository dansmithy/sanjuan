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
	
}
