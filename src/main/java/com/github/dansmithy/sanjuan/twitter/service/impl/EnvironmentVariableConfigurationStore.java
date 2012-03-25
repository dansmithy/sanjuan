package com.github.dansmithy.sanjuan.twitter.service.impl;

import com.github.dansmithy.sanjuan.twitter.service.ConfigurationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class EnvironmentVariableConfigurationStore implements ConfigurationStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentVariableConfigurationStore.class);

    private static final String CONSUMER_KEY_ENVIRONMENT_KEY = "twitter_consumer_key";
	private static final String CONSUMER_SECRET_ENVIRONMENT_KEY = "twitter_consumer_secret";
    private static final String ADMIN_CONSUMER_KEY_ENVIRONMENT_KEY = "twitter_admin_consumer_key";
    private static final String ADMIN_CONSUMER_SECRET_ENVIRONMENT_KEY = "twitter_admin_consumer_secret";
    private static final String ACCESS_TOKEN_ENVIRONMENT_KEY = "twitter_access_token";
    private static final String ACCESS_SECRET_ENVIRONMENT_KEY = "twitter_access_secret";
	private static final String SAN_JUAN_BASE_URL_ENVIRONMENT_KEY = "base_url";

    private final String consumerKey;
    private final String consumerSecret;
    private final String adminConsumerKey;
    private final String adminConsumerSecret;
    private final String accessToken;
    private final String accessSecret;
    private final String sanJuanBaseUrl;

    @Inject
    public EnvironmentVariableConfigurationStore(@Value("${" + CONSUMER_KEY_ENVIRONMENT_KEY + "}") String consumerKey,
                                                 @Value("${" + CONSUMER_SECRET_ENVIRONMENT_KEY + "}") String consumerSecret,
                                                 @Value("${" + ADMIN_CONSUMER_KEY_ENVIRONMENT_KEY + "}") String adminConsumerKey,
                                                 @Value("${" + ADMIN_CONSUMER_SECRET_ENVIRONMENT_KEY + "}") String adminConsumerSecret,
                                                 @Value("${" + ACCESS_TOKEN_ENVIRONMENT_KEY + "}") String accessToken,
                                                 @Value("${" + ACCESS_SECRET_ENVIRONMENT_KEY + "}") String accessSecret,
                                                 @Value("${" + SAN_JUAN_BASE_URL_ENVIRONMENT_KEY + "}") String sanJuanBaseUrl
                                                 ) {

        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.adminConsumerKey = adminConsumerKey;
        this.adminConsumerSecret = adminConsumerSecret;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
        this.sanJuanBaseUrl = sanJuanBaseUrl;
        
        LOGGER.info(buildPropertyOutput());
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getAdminConsumerKey() {
        return adminConsumerKey;
    }

    public String getAdminConsumerSecret() {
        return adminConsumerSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public String getSanJuanBaseUrl() {
        return sanJuanBaseUrl;
    }

    private String buildPropertyOutput() {
        StringBuilder builder = new StringBuilder();
        builder.append("Running with: \n");
        builder.append(createProperty(CONSUMER_KEY_ENVIRONMENT_KEY, this.consumerKey)).append("\n");
        builder.append(createProperty(CONSUMER_SECRET_ENVIRONMENT_KEY, this.consumerSecret)).append("\n");
        builder.append(createProperty(ADMIN_CONSUMER_KEY_ENVIRONMENT_KEY, this.adminConsumerKey)).append("\n");
        builder.append(createProperty(ADMIN_CONSUMER_SECRET_ENVIRONMENT_KEY, this.adminConsumerSecret)).append("\n");
        builder.append(createProperty(ACCESS_TOKEN_ENVIRONMENT_KEY, this.accessToken)).append("\n");
        builder.append(createProperty(ACCESS_SECRET_ENVIRONMENT_KEY, this.accessSecret)).append("\n");
        builder.append(createProperty(SAN_JUAN_BASE_URL_ENVIRONMENT_KEY, this.sanJuanBaseUrl)).append("\n");
        return builder.toString();
    }

    private String createProperty(String key, String value) {
        return String.format("[%s] = [%s]", key, value);
    }



}