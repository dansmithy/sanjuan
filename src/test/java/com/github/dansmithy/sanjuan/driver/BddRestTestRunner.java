package com.github.dansmithy.sanjuan.driver;

import com.github.dansmithy.sanjuan.bdd.SkeletonBddTestRunner;

public class BddRestTestRunner extends SkeletonBddTestRunner<GameDriver> {

	private final String baseUrl;
	private final String adminUsername;
	private final String adminPassword;

	public BddRestTestRunner(String baseUrl, String adminUsername,
			String adminPassword) {
		super();
		this.baseUrl = baseUrl;
		this.adminUsername = adminUsername;
		this.adminPassword = adminPassword;
	}

	@Override
	protected GameDriver createContext() {
		return new GameRestDriver(baseUrl, adminUsername, adminPassword);
	}

	public void afterTest(GameDriver context) {
		context.cleanup();
	}

}
