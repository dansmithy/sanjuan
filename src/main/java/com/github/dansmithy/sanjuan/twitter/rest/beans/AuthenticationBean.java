package com.github.dansmithy.sanjuan.twitter.rest.beans;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import com.github.dansmithy.sanjuan.twitter.model.TwitterUser;
import com.github.dansmithy.sanjuan.twitter.rest.jaxrs.AuthenticationResource;
import com.github.dansmithy.sanjuan.twitter.service.TwitterService;

@Named
public class AuthenticationBean implements AuthenticationResource {

	private static final String REDIRECT_URL = "/";
	
	private final TwitterService twitterService;

	@Inject
	public AuthenticationBean(TwitterService twitterService) {
		super();
		this.twitterService = twitterService;
	}

	@Override
	public Response initiateAuth() {
		return createRedirectToUrl(twitterService.getRedirectForAuthorization());
	}

	@Override
	public Response validateAuth(String tokenKey, String oauthVerifier, boolean denied) {
		if (!denied) {
			twitterService.authenticateUser(tokenKey, oauthVerifier);
		}
		return createRedirectToHome();
	}

	@Override
	public TwitterUser getUser() {
		return twitterService.getCurrentUser();
	}

	private Response createRedirectToHome() {
		return createRedirectToUrl(REDIRECT_URL);
	}

	private Response createRedirectToUrl(String url) {
		return Response.status(HttpURLConnection.HTTP_MOVED_TEMP).location(createUri(url)).build();
	}

	private URI createUri(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
