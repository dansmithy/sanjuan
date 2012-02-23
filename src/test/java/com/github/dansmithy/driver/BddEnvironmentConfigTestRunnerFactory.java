package com.github.dansmithy.driver;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.config.JunitConfiguration;
import com.github.dansmithy.rest.BddRestTestRunner;
import com.github.dansmithy.sanjuan.twitter.service.impl.BasicRoleProvider;
import com.github.dansmithy.spring.BddSpringTestRunner;
import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;

public class BddEnvironmentConfigTestRunnerFactory implements
		BddTestRunnerFactory {

	private static final String HTTP_MODE_KEY = "httpMode";
	private static final String ADMIN_USERNAME_KEY = BasicRoleProvider.getAdminUsername();
	private static final String BASE_URI_KEY = "baseUri";

	private JunitConfiguration configuration = new JunitConfiguration();

	@Override
	public BddTestRunner<GameDriver> createTestRunner() {
		if (configuration.getBooleanProperty(HTTP_MODE_KEY,
				DefaultValues.HTTP_MODE)) {
			return createHttpTestRunner();
		} else {
			return createSpringTestRunner();
		}
	}

	private BddTestRunner<GameDriver> createSpringTestRunner() {
		return new BddSpringTestRunner(configuration.getProperty(
				ADMIN_USERNAME_KEY, DefaultValues.ADMIN_USERNAME));
	}

	private BddTestRunner<GameDriver> createHttpTestRunner() {
		ClientDriver clientDriver = new ClientDriverFactory().createClientDriver(ATUtils.getRestDriverPort()); 
		return new BddRestTestRunner(configuration.getProperty(BASE_URI_KEY,
				DefaultValues.BASE_URI), configuration.getProperty(
				ADMIN_USERNAME_KEY, DefaultValues.ADMIN_USERNAME), clientDriver);
	}

}
