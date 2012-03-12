package com.github.dansmithy.sanjuan.twitter.service.impl;

import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.dao.UserDao;
import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
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
import com.github.dansmithy.sanjuan.twitter.service.ConfigurationStore;
import com.github.dansmithy.sanjuan.twitter.service.TwitterService;
import com.github.dansmithy.sanjuan.twitter.service.TwitterUserStore;
import com.github.dansmithy.sanjuan.twitter.service.exception.TwitterAuthRuntimeException;
import com.github.dansmithy.sanjuan.twitter.service.scribe.ConfigurableTwitterApi;
import org.springframework.util.Assert;

@Named
public class ScribeTwitterService implements TwitterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScribeTwitterService.class);
	
	private static final String TWITTER_CALLBACK_URL = "/ws/auth/authValidate";
	private static final String DIRECT_MESSAGE_URL = "/1/direct_messages/new.json";
	
    private static final UsernameExtractor STANDARD_USERNAME_EXTRACTOR = new RegexUsernameExtactor();

	private final TwitterUserStore twitterUserStore;
    private final ConfigurationStore configurationStore;
    private final RoleProvider roleProvider;
    private final UserDao userDao;
    private final String directMessageUrl;

	private final OAuthService oauthService;
    private final OAuthService adminOauthService;

	@Inject
	public ScribeTwitterService(TwitterUserStore twitterUserStore, ConfigurationStore configurationStore, RoleProvider roleProvider, ConfigurableTwitterApi twitterApi, UserDao userDao) {
		super();
        verifyConfiguration(configurationStore, twitterApi);
		this.twitterUserStore = twitterUserStore;
        this.configurationStore = configurationStore;
        this.roleProvider = roleProvider;
        this.userDao = userDao;

        this.oauthService = new ServiceBuilder().provider(twitterApi).apiKey(configurationStore.getConsumerKey())
				.apiSecret(configurationStore.getConsumerSecret()).callback(createCallback(configurationStore.getSanJuanBaseUrl(), TWITTER_CALLBACK_URL)).build();
        this.adminOauthService = new ServiceBuilder().provider(twitterApi).apiKey(configurationStore.getAdminConsumerKey())
                .apiSecret(configurationStore.getAdminConsumerSecret()).build();

		this.directMessageUrl = twitterApi.getTwitterBaseUrl() + DIRECT_MESSAGE_URL;
	}

    private void verifyConfiguration(ConfigurationStore configurationStore, ConfigurableTwitterApi twitterApi) {
        Assert.notNull(configurationStore.getConsumerKey(), "No Twitter Consumer Key set");
        Assert.notNull(configurationStore.getConsumerSecret(), "No Twitter Consumer Secret set");
        Assert.notNull(configurationStore.getAccessToken(), "No Twitter Access Token set");
        Assert.notNull(configurationStore.getAccessSecret(), "No Twitter Access Secret set");
        Assert.notNull(configurationStore.getSanJuanBaseUrl(), "No San Juan Base URL set");
    }

    @Override
	public String getRedirectForAuthorization() {
        try {
            Token requestToken = oauthService.getRequestToken();
            twitterUserStore.rememberToken(OAuthToken.createFromToken(requestToken));
            return oauthService.getAuthorizationUrl(requestToken);
        } catch (OAuthException oauthException) {
            throw new TwitterAuthRuntimeException("Unable to create a request token", oauthException);
        }
	}

    @Override
    public void authenticateUser(String tokenKey, String oauthVerifier) {
        Token requestToken = twitterUserStore.getOAuthToken().createToken();
        if (tokenKey == null || !tokenKey.equals(requestToken.getToken())) {
            throw new TwitterAuthRuntimeException(String.format("No token matching key [%s]", tokenKey));
        }
        authenticateRequestToken(requestToken, oauthVerifier, STANDARD_USERNAME_EXTRACTOR);
    }

    private void authenticateRequestToken(Token requestToken, String oauthVerifier, UsernameExtractor extractor) {
        try {
            Token accessToken = oauthService.getAccessToken(requestToken, new Verifier(oauthVerifier));
            String screenName = extractor.extractUsername(accessToken.getRawResponse());
            TwitterUser twitterUser = new TwitterUser(screenName, OAuthToken.createFromToken(accessToken), roleProvider.getRolesForUser(screenName));
            userDao.recordLogin(screenName);
            twitterUserStore.setCurrentUser(twitterUser);
        } catch (OAuthException oauthException) {
            throw new TwitterAuthRuntimeException("Unable to authnenticate", oauthException);
        }
    }

    @Override
    public TwitterUser getCurrentUser() {
        return twitterUserStore.getCurrentUser();
    }

    @Override
	public void sendDirectMessage(String targetUser, String message) {
        try {
            OAuthRequest oauthRequest = new OAuthRequest(Verb.POST, directMessageUrl);
            oauthRequest.addBodyParameter("screen_name", targetUser);
            oauthRequest.addBodyParameter("text", message);
            adminOauthService.signRequest(createSanJuanGameAccessToken(), oauthRequest);
            Response oauthResponse = oauthRequest.send();
            if (HttpURLConnection.HTTP_OK != oauthResponse.getCode()) {
                LOGGER.warn(String.format("Unable to send Twitter DM to user [%s] with message [%s]. Got response code [%d].", targetUser, message, oauthResponse.getCode()));
            }
        } catch (OAuthException oauthException) {
            LOGGER.warn(String.format("Unable to send Twitter DM to user [%s] with message [%s].", targetUser, message), oauthException);
        }
	}

    private Token createSanJuanGameAccessToken() {
        return new Token(configurationStore.getAccessToken(), configurationStore.getAccessSecret());
    }

	private String createCallback(String hostname, String callbackUrl) {
		return hostname + callbackUrl;
	}

    private static interface UsernameExtractor {

        String extractUsername(String response);
    }

    private static class RegexUsernameExtactor implements UsernameExtractor {

        private static final Pattern SCREEN_NAME_REGEX = Pattern.compile("screen_name=([^&]+)");

        @Override
        public String extractUsername(String response) {
            return extractUsingRegex(response, SCREEN_NAME_REGEX);
        }
    }

    private static final String extractUsingRegex(String response, Pattern p) {
        Matcher matcher = p.matcher(response);
        if (matcher.find() && matcher.groupCount() >= 1) {
            return OAuthEncoder.decode(matcher.group(1));
        } else {
            throw new TwitterAuthRuntimeException(String.format("Response body is incorrect. Can't extract details from response [%s].", response));
        }
    }

}
