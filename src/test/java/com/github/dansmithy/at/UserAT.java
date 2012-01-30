package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class UserAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	@Test
	public void testCanChangeUserPassword() {

		bdd.runTest(

				given(userExists("#alice")),

				when(updateUser("#alice",
						"{ 'username' : '#alice', 'password' : 'changedPassword' }")),

				then(verifySuccessfulResponseContains("{ 'username' : '#alice' }")));
	}

	@Test
	public void testCannotChangeUsername() {

		bdd.runTest(

				given(userExists("#alice")),

				when(updateUser("#alice",
						"{ 'username' : 'other', 'password' : 'changedPassword' }")),

				then(verifyResponseCodeIs(HTTP_BAD_REQUEST))
						.and(verifyResponseContains("{ code : 'INVALID_REQUEST' }")));
	}

	@Test
	public void testCanGetUsers() {

		bdd.runTest(
 
				given(userExists("#alice")),

				when(getUsers()),

				then(verifySuccessfulResponseContains("{ users^username: [ { 'username' : '#alice' } ] }")));
	}

}
