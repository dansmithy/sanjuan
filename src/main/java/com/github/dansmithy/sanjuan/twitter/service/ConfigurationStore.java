package com.github.dansmithy.sanjuan.twitter.service;

public interface ConfigurationStore {

	String getConsumerSecret();

	String getSanJuanBaseUrl();
    
    String getAccessToken();
    
    String getAccessSecret();

}