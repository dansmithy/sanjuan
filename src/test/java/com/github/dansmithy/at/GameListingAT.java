package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class GameListingAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	@Test
	public void testCanGetGamesForPlayer() {

		bdd.runTest(
				given(userExistsAndAuthenticated("#alice")).and(gameCreatedBy("#alice")).and(lastResponseRememberedAs("created")),

				when(getGamesFor("#alice")),

				then(verifyResponseCodeIs(HTTP_OK)).and(verifyResponse(containsGameFoundInResponse(rememberedAs("created")))));
	}

	@Test
	public void testCanGetRecruitingGames() {

		bdd.runTest(
				given(userExistsAndAuthenticated("#alice")).and(gameCreatedBy("#alice")).and(lastResponseRememberedAs("created")),

				when(getGamesInState("#alice", "RECRUITING")),

				then(verifyResponseCodeIs(HTTP_OK)).and(verifyResponse(containsGameFoundInResponse(rememberedAs("created")))));
	}

}
