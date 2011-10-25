package com.github.dansmithy.sanjuan.driver;

import com.github.dansmithy.sanjuan.bdd.SkeletonBddTestRunner;

public class BddTestRunner extends SkeletonBddTestRunner<BddContext> {

	private final String baseUrl;
	private final String adminAccountDetails;

	public BddTestRunner(String baseUrl, String adminAccountDetails) {
		this.baseUrl = baseUrl;
		this.adminAccountDetails = adminAccountDetails;
	}

	@Override
	protected BddContext createContext() {
		return new BddContext(baseUrl, adminAccountDetails);
	}

	public void afterTest(BddContext context) {
		context.cleanup();
	}

}
