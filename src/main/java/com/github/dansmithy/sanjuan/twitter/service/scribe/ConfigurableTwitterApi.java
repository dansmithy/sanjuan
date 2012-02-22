package com.github.dansmithy.sanjuan.twitter.service.scribe;

import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;

import com.google.common.base.Objects;

public class ConfigurableTwitterApi extends TwitterApi.Authenticate {

	private static final String DEFAULT_TWITTER_BASE_URL = "https://api.twitter.com";
	private static final String DEFAULT_REST_DRIVER_PORT = "48080";
	
	private static final String ENVIRONMENT_KEY_TWITTER_BASE_URL = "twitter_base_url";
	private static final String ENVIRONMENT_KEY_RESTDRIVER_PORT = "restdriver.port";
	
	private String requiredBaseUrl;
	
	public ConfigurableTwitterApi() {
		super();
		requiredBaseUrl = Objects.firstNonNull(System.getenv(ENVIRONMENT_KEY_TWITTER_BASE_URL), DEFAULT_TWITTER_BASE_URL);
		requiredBaseUrl = requiredBaseUrl.replace("%port%", getRestDriverPort());
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
		return System.getProperty(ENVIRONMENT_KEY_RESTDRIVER_PORT, DEFAULT_REST_DRIVER_PORT);
	}
}
