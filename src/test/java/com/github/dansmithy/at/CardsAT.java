package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class CardsAT {

	private static BddTestRunner<GameDriver> bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();

	@Test
	public void testGetCards() {

		bdd.runTest(
				given(noSetUp()),

				when(getCards()),

				then(verifyResponseCodeIs(HTTP_OK))
						.and(verifyResponseContains("{ 1 : 'IndigoPlant' }")));
	}

}
