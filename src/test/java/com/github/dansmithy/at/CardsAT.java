package com.github.dansmithy.at;

import static com.github.dansmithy.bdd.BddHelper.*;
import static com.github.dansmithy.bdd.GivenBddParts.*;
import static com.github.dansmithy.driver.BddPartProvider.*;
import static java.net.HttpURLConnection.*;

import org.junit.Test;

public class CardsAT extends BaseAT {

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
