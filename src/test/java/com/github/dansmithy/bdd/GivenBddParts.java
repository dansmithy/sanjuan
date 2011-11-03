package com.github.dansmithy.bdd;

import java.net.HttpURLConnection;

import com.github.dansmithy.driver.GameDriver;
import com.github.dansmithy.exception.AcceptanceTestException;

public class GivenBddParts extends SimpleBddParts<GameDriver> {

	public GivenBddParts(BddPart<GameDriver> bddPart) {
		super(bddPart);
	}
	
	public static SimpleBddParts<GameDriver> given(BddPart<GameDriver> part) {
		return new GivenBddParts(part);
	}
	
	@Override
	protected void executePart(BddPart<GameDriver> part, GameDriver context) {
		super.executePart(part, context);
		if (context.getLastResponse() != null && context.getLastResponse().getStatusCode() != HttpURLConnection.HTTP_OK) {
			throw new AcceptanceTestException(String.format("Given clause got a %d response with content %s", context.getLastResponse().getStatusCode(), context.getLastResponse().asText()));
		}
	}

	
}
