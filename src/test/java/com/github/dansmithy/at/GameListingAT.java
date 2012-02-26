package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.Test;

public class GameListingAT extends BaseAT {
	
	@Test
	public void testCanGetGamesForPlayer() {

		bdd.runTest(
				given(userAuthenticated("#alice")).and(
						gameCreatedBy("#alice")).and(
						lastResponseRememberedAs("created")),

				when(getGamesFor("#alice", loggedInAs("#alice"))),

				then(verifyResponseCodeIs(HTTP_OK))
						.and(verifyResponse(containsGameFoundInResponse(rememberedAs("created")))));
	}

	@Test
	public void testCanGetRecruitingGames() {

		bdd.runTest(
				given(userAuthenticated("#alice")).and(
						gameCreatedBy("#alice")).and(
						lastResponseRememberedAs("created")),

				when(getGamesInState("#alice", "RECRUITING")),

				then(verifyResponseCodeIs(HTTP_OK))
						.and(verifyResponse(containsGameFoundInResponse(rememberedAs("created")))));
	}

	@Test
	public void testCanGetAllGames() {

		bdd.runTest(
				given(userAuthenticated("#alice")).and(
						gameCreatedBy("#alice")).and(
						lastResponseRememberedAs("created")),

				when(getAllGames("#alice")),

				then(verifyResponseCodeIs(HTTP_OK))
						.and(verifyResponse(containsGameFoundInResponse(rememberedAs("created")))));
	}

	@Test
	public void testCannotGetGamesForOtherPlayer() {

		bdd.runTest(
				given(userAuthenticated("#alice"))
						.and(userAuthenticated("#bob"))
						.and(gameCreatedBy("#alice"))
						.and(lastResponseRememberedAs("created")),

				when(getGamesFor("#alice", loggedInAs("#bob"))),

				then(verifyResponseCodeIs(HTTP_UNAUTHORIZED))
						.and(verifyResponseContains("{ code : 'NOT_MATCHING_PLAYER' }")));
	}

	@Test
	public void testGetNotFoundWhenGameDoesNotExist() {

		bdd.runTest(
				given(userAuthenticated("#alice")),

				when(getGame("#alice", 0)),

				then(verifyResponseCodeIs(HTTP_NOT_FOUND))
						.and(verifyResponseContains("{ code : 'NOT_FOUND' }")));
	}

}
