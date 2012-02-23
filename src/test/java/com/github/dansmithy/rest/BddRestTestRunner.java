package com.github.dansmithy.rest;

import com.github.dansmithy.bdd.SkeletonBddTestRunner;
import com.github.dansmithy.driver.GameDriver;
import com.github.restdriver.clientdriver.ClientDriver;

public class BddRestTestRunner extends SkeletonBddTestRunner<GameDriver> {

	private final String baseUrl;
	private final String adminUsername;
	private final ClientDriver clientDriver;

	public BddRestTestRunner(String baseUrl, String adminUsername, ClientDriver clientDriver) {
		super();
		this.baseUrl = baseUrl;
		this.adminUsername = adminUsername;
		this.clientDriver = clientDriver;
	}

	@Override
	public GameDriver createContext() {
		return new GameRestDriver(baseUrl, adminUsername, clientDriver);
	}

	public void afterTest(GameDriver context) {
		context.cleanup();
	}
}
