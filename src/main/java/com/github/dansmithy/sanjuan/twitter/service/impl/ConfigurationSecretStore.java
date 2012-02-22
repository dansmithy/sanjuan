package com.github.dansmithy.sanjuan.twitter.service.impl;

import java.io.File;
import java.io.IOException;

import javax.inject.Named;

import org.apache.commons.io.FileUtils;

import com.github.dansmithy.sanjuan.twitter.service.SecretStore;
import com.google.common.base.Objects;

@Named
public class ConfigurationSecretStore implements SecretStore {

	private static final String CONSUMER_KEY_ENVIRONMENT_KEY = "twitter_consumer_key";
	private static final String BASE_URL_ENVIRONMENT_KEY = "base_url";
	
	private static final String DEFAULT_BASE_URL = "http://localhost:8086";
	
	/* (non-Javadoc)
	 * @see com.github.dansmithy.twitterlogin.service.impl.SecretStore#getConsumerKey()
	 */
	@Override
	public String getConsumerKey() {
		String environmentValue = System.getenv(CONSUMER_KEY_ENVIRONMENT_KEY);
		String filesystemValue = getFilesystemValue();
		try {
			return Objects.firstNonNull(environmentValue, filesystemValue);
		} catch (NullPointerException e) {
			throw new RuntimeException("Cannot read twitter consumer key either from system property or from file", e);
		}
	}
	
	private String getFilesystemValue() {
		String userDir = System.getProperty("user.home");
		String userFile = userDir + "/twitter.consumer.key";
		try {
			return FileUtils.readFileToString(new File(userFile));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String getBaseUrl() {
		return Objects.firstNonNull(System.getenv(BASE_URL_ENVIRONMENT_KEY), DEFAULT_BASE_URL);
	}

}
