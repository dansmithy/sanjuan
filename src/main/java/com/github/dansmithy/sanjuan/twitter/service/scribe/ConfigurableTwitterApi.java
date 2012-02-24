package com.github.dansmithy.sanjuan.twitter.service.scribe;

import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableTwitterApi extends TwitterApi.Authenticate {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurableTwitterApi.class);
	
	private static final String DEFAULT_TWITTER_BASE_URL = "https://api.twitter.com";
	private static final String DEFAULT_RESTDRIVER_PORT = "48080";
	
	private static final String ENVIRONMENT_KEY_TWITTER_BASE_URL = "twitter.baseurl";
	private static final String ENVIRONMENT_KEY_RESTDRIVER_PORT = "restdriver.port";

	
	
	private String requiredBaseUrl;
	
	public ConfigurableTwitterApi() {
		super();
		requiredBaseUrl = System.getProperty(ENVIRONMENT_KEY_TWITTER_BASE_URL, DEFAULT_TWITTER_BASE_URL);
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
	
	public static String getRestDriverPort() {
		return System.getProperty(ENVIRONMENT_KEY_RESTDRIVER_PORT, DEFAULT_RESTDRIVER_PORT);
	}
}
