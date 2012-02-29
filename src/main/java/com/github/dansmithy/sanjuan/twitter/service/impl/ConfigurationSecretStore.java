package com.github.dansmithy.sanjuan.twitter.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.inject.Named;

import org.apache.commons.io.FileUtils;

import com.github.dansmithy.sanjuan.twitter.service.SecretStore;
import com.google.common.base.Objects;

@Named
public class ConfigurationSecretStore implements SecretStore {

	private static final String CONSUMER_KEY_ENVIRONMENT_KEY = "twitter_consumer_key";
    private static final String ACCESS_TOKEN_ENVIRONMENT_KEY = "twitter_access_token";
    private static final String ACCESS_SECRET_ENVIRONMENT_KEY = "twitter_access_secret";
	private static final String BASE_URL_ENVIRONMENT_KEY = "base_url";
	
	private static final String DEFAULT_BASE_URL = "http://localhost:8086";
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.twitterlogin.service.impl.SecretStore#getConsumerKey()
	 */
	@Override
	public String getConsumerKey() {
        return getConfiguredValueUsingFilesystemDefault(CONSUMER_KEY_ENVIRONMENT_KEY);
	}

    @Override
    public String getBaseUrl() {
        return getConfiguredValue(BASE_URL_ENVIRONMENT_KEY, DEFAULT_BASE_URL);
    }

    @Override
    public String getAccessToken() {
        return getConfiguredValueUsingFilesystemDefault(ACCESS_TOKEN_ENVIRONMENT_KEY);
    }

    @Override
    public String getAccessSecret() {
        return getConfiguredValueUsingFilesystemDefault(ACCESS_SECRET_ENVIRONMENT_KEY);
    }

    private String getConfiguredValueUsingFilesystemDefault(String key) {
        return getConfiguredValue(key, getFilesystemValue(key));
    }

    private String getConfiguredValue(String key, String defaultValue) {
        String environmentValue = System.getenv(key);
        try {
            return Objects.firstNonNull(environmentValue, defaultValue);
        } catch (NullPointerException e) {
            throw new RuntimeException(String.format("Cannot find value for [%s] as environment variable or otherwise", key), e);
        }
    }

    private String getFilesystemValue(String key) {
		String userDir = System.getProperty("user.home");
		String userFile = userDir + "/sanjuan.secrets.properties";
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(userFile)));
        } catch (IOException e) {
            return null;
        }
        return properties.getProperty(key);
	}

}
