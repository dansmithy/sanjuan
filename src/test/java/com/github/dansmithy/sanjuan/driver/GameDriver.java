package com.github.dansmithy.sanjuan.driver;

import static com.github.restdriver.serverdriver.RestServerDriver.body;
import static com.github.restdriver.serverdriver.RestServerDriver.header;
import static com.github.restdriver.serverdriver.RestServerDriver.post;

import java.util.List;

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;


public class GameDriver {

	private static final String JSON_CONTENT_TYPE = "application/json";
	private static final Header ACCEPT_JSON_HEADER = header("Accept", JSON_CONTENT_TYPE);

	private String baseUri;
	
	public GameDriver(String baseUri) {
		super();
		this.baseUri = baseUri;
	}

	public GameDriverSession login(String data) {
		TranslatedValues translatedValues = new TranslatedValues();
		return login(data, translatedValues);
	}
	
	public GameDriverSession login(String data, TranslatedValues translatedValues) {
		RequestValues requestValues = translatedValues.translateRequestValues(new RequestValues().addAll(DefaultValues.USER).addReadableData(data));
		
		Response response = post(baseUri + "/j_spring_security_check", body(requestValues.toJson(), JSON_CONTENT_TYPE), ACCEPT_JSON_HEADER);
		List<Header> cookieData = response.getHeaders("Set-Cookie");
		String sessionId = extractJSessionId(cookieData);
		
		return new GameDriverSession(baseUri, sessionId, translatedValues);
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
