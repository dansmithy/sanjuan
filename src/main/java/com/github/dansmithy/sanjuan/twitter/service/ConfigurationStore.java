package com.github.dansmithy.sanjuan.twitter.service;

public interface ConfigurationStore {

    String getConsumerKey();

	String getConsumerSecret();

	String getSanJuanBaseUrl();
    
    String getAccessToken();
    
    String getAccessSecret();

}