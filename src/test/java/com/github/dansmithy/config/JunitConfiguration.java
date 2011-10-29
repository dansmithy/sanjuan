package com.github.dansmithy.config;


public class JunitConfiguration {

	public String getProperty(String key, String defaultValue) {
		String envValue = System.getenv(key);
		return envValue == null ? defaultValue : envValue;
	}
	
	public Boolean getBooleanProperty(String key, Boolean defaultValue) {
		return Boolean.valueOf(getProperty(key, defaultValue.toString()));
	}
}
