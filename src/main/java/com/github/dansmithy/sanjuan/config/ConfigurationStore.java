package com.github.dansmithy.sanjuan.config;

public interface ConfigurationStore {

    String getConsumerKey();

	String getConsumerSecret();

    String getAdminConsumerKey();

    String getAdminConsumerSecret();

	String getSanJuanBaseUrl();
    
    String getAccessToken();
    
    String getAccessSecret();

    String getTwitterBaseUrl();
}