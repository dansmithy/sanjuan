package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.given;
import static com.github.dansmithy.bdd.BddHelper.then;
import static com.github.dansmithy.bdd.BddHelper.when;
import static com.github.dansmithy.driver.BddPartProvider.gameCreatedBy;
import static com.github.dansmithy.driver.BddPartProvider.gameOwnedByJoinedBy;
import static com.github.dansmithy.driver.BddPartProvider.gameStartedBy;
import static com.github.dansmithy.driver.BddPartProvider.roleChosenBy;
import static com.github.dansmithy.driver.BddPartProvider.userExistsAndAuthenticated;
import static com.github.dansmithy.driver.BddPartProvider.verifySuccessfulResponseContains;

import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class GovernorAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	@Test
	public void testCanChooseRole() {

		bdd.runTest(

				given(userExistsAndAuthenticated("#alice"))
						.and(userExistsAndAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(gameOwnedByJoinedBy("#alice", "#bob"))
						.and(gameStartedBy("#alice")),

				when(roleChosenBy("#alice", "round : 1; phase : 1",
						"role : BUILDER")),

				then(verifySuccessfulResponseContains("{ 'state' : 'PLAYING', 'players^name' : [ { 'name' : '#alice', victoryPoints: 1 }, { 'name' : '#bob', victoryPoints: 1 } ], 'roundNumber' : 1, 'rounds^state' : [ { 'state' : 'PLAYING', phases^state : [ { 'state' : 'PLAYING', plays : [ { 'state' : 'AWAITING_INPUT' } ] } ] } ] }")));
	}

}
