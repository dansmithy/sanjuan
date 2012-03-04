package com.github.dansmithy.sanjuan.twitter.service.scribe;

import javax.inject.Named;

import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

@Named
public class ConfigurableTwitterApi extends TwitterApi.Authenticate {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurableTwitterApi.class);
	
	private static final String DEFAULT_TWITTER_BASE_URL = "https://api.twitter.com";

	private static final String ENVIRONMENT_KEY_TWITTER_BASE_URL = "twitter.baseurl";

	private String requiredBaseUrl;
	
    public ConfigurableTwitterApi() {
        super();
        requiredBaseUrl = initializeTwitterBaseUrl();
        LOGGER.info(String.format("Using Twitter Base URL [%s].", requiredBaseUrl));
    }

    @Override
	public String getAuthorizationUrl(Token requestToken) {
		String url = super.getAuthorizationUrl(requestToken);
		return processTwitterUrl(url);
	}

	@Override
	public String getAccessTokenEndpoint() {
		String url = super.getAccessTokenEndpoint();
		return processTwitterUrl(url);
	}

	@Override
	public String getRequestTokenEndpoint() {
		String url = super.getRequestTokenEndpoint();
		return processTwitterUrl(url);
	}

	private String processTwitterUrl(String url) {
		return url.replace(DEFAULT_TWITTER_BASE_URL, requiredBaseUrl);
	}

	public String getTwitterBaseUrl() {
		return requiredBaseUrl;
	}

    public static String initializeTwitterBaseUrl() {
        String environmentVariable = System.getenv(ENVIRONMENT_KEY_TWITTER_BASE_URL);
        String systemProperty = System.getProperty(ENVIRONMENT_KEY_TWITTER_BASE_URL, DEFAULT_TWITTER_BASE_URL);
        LOGGER.info(String.format("Environment variable for [%s] is [%s], System property is [%s]", ENVIRONMENT_KEY_TWITTER_BASE_URL, environmentVariable, systemProperty));
        return Objects.firstNonNull(environmentVariable, systemProperty);
    }


}
