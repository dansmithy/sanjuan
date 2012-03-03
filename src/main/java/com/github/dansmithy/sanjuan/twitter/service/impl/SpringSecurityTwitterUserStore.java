package com.github.dansmithy.sanjuan.twitter.service.impl;

import javax.inject.Named;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.dansmithy.sanjuan.twitter.model.OAuthToken;
import com.github.dansmithy.sanjuan.twitter.model.TwitterUser;
import com.github.dansmithy.sanjuan.twitter.service.TwitterUserStore;
import com.github.dansmithy.sanjuan.twitter.service.exception.TwitterAuthRuntimeException;

@Named
public class SpringSecurityTwitterUserStore implements TwitterUserStore {

	@Override
	public void setCurrentUser(TwitterUser user) {
		getContext().setAuthentication(user);
	}

	@Override
	public TwitterUser getCurrentUser() {
		Object o = getContext().getAuthentication();
		if (o instanceof TwitterUser) {
			return (TwitterUser) o;
		}
		return TwitterUser.EMPTY_USER;
	}

	private SecurityContext getContext() {
		return SecurityContextHolder.getContext();
	}

	@Override
	public void rememberToken(OAuthToken token) {
		setCurrentUser(new TwitterUser.PotentialTwitterUser(token));
	}

	@Override
	public OAuthToken getOAuthToken() {
		return getCurrentUser().getAccessToken();
	}

}
