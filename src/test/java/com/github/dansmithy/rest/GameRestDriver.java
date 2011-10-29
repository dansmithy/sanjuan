package com.github.dansmithy.rest;

import static com.github.restdriver.serverdriver.RestServerDriver.body;
import static com.github.restdriver.serverdriver.RestServerDriver.header;
import static com.github.restdriver.serverdriver.RestServerDriver.post;

import java.util.List;

import com.github.dansmithy.driver.GameDriverSession;
import com.github.dansmithy.driver.RequestValues;
import com.github.dansmithy.driver.SkeletonGameDriver;
import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;


public class GameRestDriver extends SkeletonGameDriver {

	private static final String JSON_CONTENT_TYPE = "application/json";
	private static final Header ACCEPT_JSON_HEADER = header("Accept", JSON_CONTENT_TYPE);

	private String baseUri;
	
	public GameRestDriver(String baseUri, String adminUsername, String adminPassword) {
		super(adminUsername, adminPassword);
		this.baseUri = baseUri;
		createAdminSession();
	}

	@Override
	protected GameDriverSession login(String username, String password) {
		RequestValues requestValues = createTranslatedUserRequest(username, password);
		Response response = post(baseUri + "/j_spring_security_check", body(requestValues.toJson(), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER);
		List<Header> cookieData = response.getHeaders("Set-Cookie");
		String sessionId = extractJSessionId(cookieData);
		return new GameRestDriverSession(baseUri, sessionId, getTranslatedValues());
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

}
