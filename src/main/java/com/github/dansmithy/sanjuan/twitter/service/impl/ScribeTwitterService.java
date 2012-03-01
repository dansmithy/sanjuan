package com.github.dansmithy.sanjuan.twitter.service.impl;

import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.dao.UserDao;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dansmithy.sanjuan.twitter.model.OAuthToken;
import com.github.dansmithy.sanjuan.twitter.model.TwitterUser;
import com.github.dansmithy.sanjuan.twitter.service.RoleProvider;
import com.github.dansmithy.sanjuan.twitter.service.SecretStore;
import com.github.dansmithy.sanjuan.twitter.service.TwitterService;
import com.github.dansmithy.sanjuan.twitter.service.TwitterUserStore;
import com.github.dansmithy.sanjuan.twitter.service.exception.TwitterAuthRuntimeException;
import com.github.dansmithy.sanjuan.twitter.service.scribe.ConfigurableTwitterApi;

@Named
public class ScribeTwitterService implements TwitterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScribeTwitterService.class);
	
	private static final String TWITTER_CONSUMER_KEY = "nYOW3tE8e96R7px104ez1w";
	
	private static final String TWITTER_CALLBACK_URL = "/ws/auth/authValidate";
	private static final String DIRECT_MESSAGE_URL = "/1/direct_messages/new.json";
	
	private static final Pattern SCREEN_NAME_REGEX = Pattern.compile("screen_name=([^&]+)");

	private final TwitterUserStore twitterUserStore;
	private final OAuthService oauthService;
    private final SecretStore secretStore;
    private final RoleProvider roleProvider;
    private final UserDao userDao;
    private final String directMessageUrl;
	
	@Inject
	public ScribeTwitterService(TwitterUserStore twitterUserStore, SecretStore secretStore, RoleProvider roleProvider, ConfigurableTwitterApi twitterApi, UserDao userDao) {
		super();
		this.twitterUserStore = twitterUserStore;
        this.secretStore = secretStore;
        this.roleProvider = roleProvider;
        this.userDao = userDao;
        this.oauthService = new ServiceBuilder().provider(twitterApi).apiKey(TWITTER_CONSUMER_KEY)
				.apiSecret(secretStore.getConsumerKey()).callback(createCallback(secretStore.getBaseUrl(), TWITTER_CALLBACK_URL)).build();
		this.directMessageUrl = twitterApi.getTwitterBaseUrl() + DIRECT_MESSAGE_URL;
	}

	@Override
	public String getRedirectForAuthorization() {
		Token requestToken = oauthService.getRequestToken();
		twitterUserStore.rememberToken(OAuthToken.createFromToken(requestToken));
		return oauthService.getAuthorizationUrl(requestToken);
	}

	@Override
	public void authenticateUser(String tokenKey, String oauthVerifier) {
		Token requestToken = twitterUserStore.getOAuthToken().createToken();
		if (tokenKey == null || !tokenKey.equals(requestToken.getToken())) {
			throw new TwitterAuthRuntimeException(String.format("No token matching key [%s]", tokenKey));
		}
		Token accessToken = oauthService.getAccessToken(requestToken, new Verifier(oauthVerifier));
		String screenName = extractUsingRegex(accessToken.getRawResponse(), SCREEN_NAME_REGEX);
		TwitterUser twitterUser = new TwitterUser(screenName, OAuthToken.createFromToken(accessToken), roleProvider.getRolesForUser(screenName));
        userDao.recordLogin(screenName);
		twitterUserStore.setCurrentUser(twitterUser);
	}

    @Override
    public TwitterUser getCurrentUser() {
        return twitterUserStore.getCurrentUser();
    }

    @Override
	public void sendDirectMessage(String targetUser, String message) {
		OAuthRequest oauthRequest = new OAuthRequest(Verb.POST, directMessageUrl);
		oauthRequest.addBodyParameter("screen_name", targetUser);
		oauthRequest.addBodyParameter("text", message);
		oauthService.signRequest(createSanJuanGameAccessToken(), oauthRequest);
		Response oauthResponse = oauthRequest.send();
		if (HttpURLConnection.HTTP_OK != oauthResponse.getCode()) {
			LOGGER.error(String.format("Unable to send Twitter DM to user [%s] with message [%s]. Got response code [%d].", targetUser, message, oauthResponse.getCode()));
		}
	}
    
    private Token createSanJuanGameAccessToken() {
        return new Token(secretStore.getAccessToken(), secretStore.getAccessSecret());
    }
	
	private String extractUsingRegex(String response, Pattern p) {
		Matcher matcher = p.matcher(response);
		if (matcher.find() && matcher.groupCount() >= 1) {
			return OAuthEncoder.decode(matcher.group(1));
		} else {
			throw new TwitterAuthRuntimeException("Response body is incorrect. Can't extract details from response", null);
		}
	}
	
	private String createCallback(String hostname, String callbackUrl) {
		return hostname + callbackUrl;
	}
	
}
