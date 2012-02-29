package com.github.dansmithy.sanjuan.twitter.service;

public interface SecretStore {

	String getConsumerKey();

	String getBaseUrl();
    
    String getAccessToken();
    
    String getAccessSecret();

}