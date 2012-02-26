package com.github.dansmithy.at;

import org.junit.After;
import org.junit.Before;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.driver.BddEnvironmentConfigTestRunnerFactory;
import com.github.dansmithy.driver.GameDriver;

public abstract class BaseAT {

	protected BddTestRunner<GameDriver> bdd;

	@Before
	public void createTestRunner() {
		 bdd = new BddEnvironmentConfigTestRunnerFactory()
			.createTestRunner();
	}
	
	@After
	public void stopTestRunner() {
		bdd.shutdown();
	}
}
