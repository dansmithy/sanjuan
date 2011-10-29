package com.github.dansmithy.driver;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.config.JunitConfiguration;
import com.github.dansmithy.rest.BddRestTestRunner;
import com.github.dansmithy.spring.BddSpringTestRunner;

public class BddEnvironmentConfigTestRunnerFactory implements
		BddTestRunnerFactory {

	private static final String HTTP_MODE_KEY = "httpMode";
	private static final String ADMIN_USERNAME_KEY = "adminUsername";
	private static final String ADMIN_PASSWORD_KEY = "adminPassword";
	private static final String BASE_URI_KEY = "baseUri";

	private Boolean httpMode = Boolean.TRUE;
	private String adminUsername = "danny";
	private String adminPassword = "danny";
	private String baseUri = "http://localhost:8086";

	private JunitConfiguration configuration = new JunitConfiguration();

	@Override
	public BddTestRunner<GameDriver> createTestRunner() {
		if (configuration.getBooleanProperty(HTTP_MODE_KEY, httpMode)) {
			return createHttpTestRunner();
		} else {
			return createSpringTestRunner();
		}
	}

	private BddTestRunner<GameDriver> createSpringTestRunner() {
		return new BddSpringTestRunner(configuration.getProperty(
				ADMIN_USERNAME_KEY, adminUsername), configuration.getProperty(
				ADMIN_PASSWORD_KEY, adminPassword));
	}

	private BddTestRunner<GameDriver> createHttpTestRunner() {
		return new BddRestTestRunner(configuration.getProperty(BASE_URI_KEY,
				baseUri), configuration.getProperty(ADMIN_USERNAME_KEY,
				adminUsername), configuration.getProperty(ADMIN_PASSWORD_KEY,
				adminPassword));
	}

}
