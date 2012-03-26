package com.github.dansmithy.sanjuan.twitter.service.scribe;

import com.github.dansmithy.sanjuan.twitter.service.ConfigurationStore;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ConfigurableTwitterApi extends TwitterApi.Authenticate {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurableTwitterApi.class);

    private static final String DEFAULT_TWITTER_BASE_URL = "https://api.twitter.com";
	
	private String requiredBaseUrl;

    @Inject
    public ConfigurableTwitterApi(ConfigurationStore configurationStore) {
        requiredBaseUrl = configurationStore.getTwitterBaseUrl();
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

}
