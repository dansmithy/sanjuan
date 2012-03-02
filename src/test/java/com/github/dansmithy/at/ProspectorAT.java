package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;

import org.junit.Test;

public class ProspectorAT extends BaseAT {
	
	@Test
	public void testLeadPlayerGetsACard() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(gameBegunWithTwoPlayers("#alice", "#bob")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : PROSPECTOR")),

				then(verifySuccessfulResponseContains("{ 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING', currentPlay : { 'state' : 'AWAITING_INPUT' } } } }")));
	}

	@Test
	public void testCanPickUpProspectedCards() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted()).and(gameBegunWithTwoPlayers("#alice", "#bob")).and(
						roleChosenBy("#alice", "round : 1; phase : 1",
								"role : PROSPECTOR")),

				when(userPlays("#alice", "round : 1; phase : 1; play : 1",
						"{}")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'currentRound' : { 'state' : 'PLAYING', currentPhase : { 'state' : 'PLAYING' } } }")));
		
		//currentPlay : [ { 'state' : 'COMPLETED', 'offered' : { 'prospected' : [ '#well' ] } }
	}



}
