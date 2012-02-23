package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public class CardsAT {

	private static BddTestRunner<GameDriver> bdd;

	@BeforeClass
	public static void createTestRunner() {
		 bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();
	}
	
	@AfterClass
	public static void stopTestRunner() {
		bdd.shutdown();
	}

	@Test
	public void testGetCards() {

		bdd.runTest(
				given(noSetUp()),

				when(getCards()),

				then(verifyResponseCodeIs(HTTP_OK))
						.and(verifyResponseContains("{ 1 : 'IndigoPlant' }")));
	}

	@Test
	public void testGetCardTypes() {

		bdd.runTest(
				given(noSetUp()),

				when(getCardTypes()),

				then(verifyResponseCodeIs(HTTP_OK))
						.and(verifyResponseContains("{ 'IndigoPlant' : { buildingCost : 1, victoryPoints : 1, category: 'PRODUCTION' } }")));
	}
}
