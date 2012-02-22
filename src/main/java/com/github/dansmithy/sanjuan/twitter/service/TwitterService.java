package com.github.dansmithy.sanjuan.twitter.service;

import com.github.dansmithy.sanjuan.twitter.model.TwitterUser;

public interface TwitterService {

	String getRedirectForAuthorization();
	
	void authenticateUser(String tokenKey, String oauthVerifier);
	
	TwitterUser getCurrentUser();
	
	void sendDirectMessage(String targetUser, String message);
}
