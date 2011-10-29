package com.github.dansmithy.driver;

import com.github.dansmithy.bdd.BddTestRunner;

public interface BddTestRunnerFactory {

	BddTestRunner<GameDriver> createTestRunner();
}
