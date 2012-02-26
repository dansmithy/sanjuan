package com.github.dansmithy.rest;

import static com.github.restdriver.clientdriver.RestClientDriver.*;
import static com.github.restdriver.serverdriver.RestServerDriver.*;
import static org.hamcrest.Matchers.*;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Assert;

import com.github.dansmithy.driver.ATUtils;
import com.github.dansmithy.driver.GameDriverSession;
import com.github.dansmithy.driver.SkeletonGameDriver;
import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;


public class GameRestDriver extends SkeletonGameDriver {

	private static final String JSON_CONTENT_TYPE = "application/json";
	private static final Header ACCEPT_JSON_HEADER = header("Accept", JSON_CONTENT_TYPE);

	private String baseUri;
	private ClientDriver clientDriver;
	
	public GameRestDriver(String baseUri, String adminUsername, ClientDriver clientDriver) {
		super(adminUsername);
		this.baseUri = baseUri;
		this.clientDriver = clientDriver;
		createAdminSession();
	}

	@Override
	protected GameDriverSession login(String username, boolean isAdmin) {
		String aliasUsername = getTranslatedValues().alias(username);
		String requestToken = "request_token";
		String requestTokenSecret = "request_token_secret";
		String accessToken = "access_token";
		String accessTokenSecret = "access_token_secret";
		String accessTokenVerifier = "access_token_verifier";
		String userId = "user_id";
		String requestTokenReponseBody = String.format("oauth_token=%s&oauth_token_secret=%s&oauth_callback_confirmed=true", requestToken, requestTokenSecret);
		clientDriver.addExpectation(onRequestTo("/oauth/request_token").withMethod(Method.POST), giveResponse(requestTokenReponseBody).withStatus(HttpURLConnection.HTTP_OK));
		Response requestTokenResponse = get(baseUri + "/ws/auth/authToken");
		List<Header> cookieData = requestTokenResponse.getHeaders("Set-Cookie");
		String sessionId = extractJSessionId(cookieData);
		GameRestDriverSession session = new GameRestDriverSession(baseUri, sessionId, getTranslatedValues());
		Assert.assertThat("Expected to get 302 response code when requesting authToken", requestTokenResponse.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_MOVED_TEMP)));
		String twitterLoginUrl = requestTokenResponse.getHeader("location").getValue();
		Assert.assertThat(twitterLoginUrl, is(equalTo(String.format("http://localhost:%d/oauth/authenticate?oauth_token=%s", ATUtils.getRestDriverPort(), requestToken))));
		String accessTokenReponseBody = String.format("oauth_token=%s&oauth_token_secret=%s&user_id=%s&screen_name=%s", accessToken, accessTokenSecret, userId, aliasUsername);
		clientDriver.addExpectation(onRequestTo("/oauth/access_token").withMethod(Method.POST), giveResponse(accessTokenReponseBody).withStatus(HttpURLConnection.HTTP_OK));
		Response validateUserResponse = get(String.format(baseUri + "/ws/auth/authValidate?oauth_token=%s&oauth_verifier=%s", requestToken, accessTokenVerifier), session.createSessionHeader());
		Assert.assertThat(validateUserResponse.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_MOVED_TEMP)));
		return session;
	}	
	
	@Override
	public void allowAllTwitterMessages() {
		clientDriver.addExpectation(onRequestTo("/1/direct_messages/new.json").withMethod(Method.POST).withParam("screen_name", Pattern.compile(".*")).withParam("text", Pattern.compile(".*")), giveEmptyResponse().withStatus(HttpURLConnection.HTTP_OK)).anyTimes();
	}
	
	private String extractJSessionId(List<Header> headers) {
		for (Header header : headers) {
			String[] parts = header.getValue().split(";");
			for (String part : parts) {
				String[] tokens = part.split("=");
				if ("JSESSIONID".equals(tokens[0])) {
					return tokens[1];
				}
			}
		}
		return "";
	}

	@Override
	public Response getCards() {
		return get(baseUri + "/ws/cards", ACCEPT_JSON_HEADER);
	}

	@Override
	public Response getCardTypes() {
		return get(baseUri + "/ws/cards/types", ACCEPT_JSON_HEADER);
	}

}
