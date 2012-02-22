package com.github.dansmithy.sanjuan.twitter.service;

import com.github.dansmithy.sanjuan.twitter.model.OAuthToken;
import com.github.dansmithy.sanjuan.twitter.model.TwitterUser;

public interface TwitterUserStore {

	void setCurrentUser(TwitterUser user);
	
	TwitterUser getCurrentUser();
	
	void rememberToken(OAuthToken token);
	
	OAuthToken getOAuthToken();
}
