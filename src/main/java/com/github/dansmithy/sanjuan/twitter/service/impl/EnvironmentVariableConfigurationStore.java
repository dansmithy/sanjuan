package com.github.dansmithy.sanjuan.twitter.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.twitter.service.ConfigurationStore;
import com.google.common.base.Objects;
import org.apache.commons.lang.StringUtils;

@Named
public class EnvironmentVariableConfigurationStore implements ConfigurationStore {

    private static final String CONSUMER_KEY_ENVIRONMENT_KEY = "twitter_consumer_key";
	private static final String CONSUMER_SECRET_ENVIRONMENT_KEY = "twitter_consumer_secret";
    private static final String ADMIN_CONSUMER_KEY_ENVIRONMENT_KEY = "twitter_admin_consumer_key";
    private static final String ADMIN_CONSUMER_SECRET_ENVIRONMENT_KEY = "twitter_admin_consumer_secret";
    private static final String ACCESS_TOKEN_ENVIRONMENT_KEY = "twitter_access_token";
    private static final String ACCESS_SECRET_ENVIRONMENT_KEY = "twitter_access_secret";
	private static final String SAN_JUAN_BASE_URL_ENVIRONMENT_KEY = "base_url";

    private static final String SUPPRESS_EXCEPTIONS_SYSTEM_KEY = "suppress_missing_value_exceptions";

	private static final String DEFAULT_BASE_URL = "http://localhost:8086";
    private static final String DEFAULT_DUMMY_VALUE = "empty value";


    @Override
    public String getSanJuanBaseUrl() {
        return getConfiguredValue(SAN_JUAN_BASE_URL_ENVIRONMENT_KEY, DEFAULT_BASE_URL);
    }

    @Override
    public String getConsumerKey() {
        return getConfiguredValueUsingFilesystemDefault(CONSUMER_KEY_ENVIRONMENT_KEY);
    }

    /* (non-Javadoc)
      * @see com.github.dansmithy.twitterlogin.service.impl.ConfigurationStore#getConsumerSecret()
      */
	@Override
	public String getConsumerSecret() {
        return getConfiguredValueUsingFilesystemDefault(CONSUMER_SECRET_ENVIRONMENT_KEY);
	}

    @Override
    public String getAdminConsumerKey() {
        return getConfiguredValueUsingFilesystemDefault(ADMIN_CONSUMER_KEY_ENVIRONMENT_KEY);
    }

    @Override
    public String getAdminConsumerSecret() {
        return getConfiguredValueUsingFilesystemDefault(ADMIN_CONSUMER_SECRET_ENVIRONMENT_KEY);
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
            if (shouldThrowExceptionsOnMissingValues()) {
                throw new RuntimeException(String.format("Cannot find value for [%s] as environment variable or otherwise", key), e);
            } else {
                return DEFAULT_DUMMY_VALUE;
            }
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

    private boolean shouldThrowExceptionsOnMissingValues() {
        return StringUtils.isEmpty(System.getProperty(SUPPRESS_EXCEPTIONS_SYSTEM_KEY));
    }

}
