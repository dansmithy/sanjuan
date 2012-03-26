package com.github.dansmithy.driver;

import com.github.dansmithy.util.ATUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dansmithy.bdd.BddTestRunner;
import com.github.dansmithy.config.JunitConfiguration;
import com.github.dansmithy.rest.BddRestTestRunner;
import com.github.dansmithy.spring.BddSpringTestRunner;
import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;

import static com.github.restdriver.serverdriver.RestServerDriver.get;

public class BddEnvironmentConfigTestRunnerFactory implements
		BddTestRunnerFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BddEnvironmentConfigTestRunnerFactory.class);

	private static final String HTTP_MODE_KEY = "httpMode";
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
		return new BddSpringTestRunner();
	}

	private BddTestRunner<GameDriver> createHttpTestRunner() {
        String sanJuanBaseUrl = configuration.getProperty(BASE_URI_KEY,
                DefaultValues.BASE_URI);
        String twitterBaseUrl = get(sanJuanBaseUrl + "/ws/admin/twitter.baseurl").getContent();
		int port = ATUtils.extractRestDriverPort(twitterBaseUrl);
		LOGGER.info(String.format("Starting ClientDriver on port [%d]", port));
        String adminUsername = get(sanJuanBaseUrl + "/ws/admin/follow").getContent();
		ClientDriver clientDriver = new ClientDriverFactory().createClientDriver(port);
		return new BddRestTestRunner(sanJuanBaseUrl, adminUsername, clientDriver);
	}

}
