package com.github.dansmithy.sanjuan.twitter.model;

import org.scribe.model.Token;

public class OAuthToken {

	private String token;
	private String secret;

	public OAuthToken(String token, String secret) {
		super();
		this.token = token;
		this.secret = secret;
	}

	public String getToken() {
		return token;
	}

	public String getSecret() {
		return secret;
	}

	public Token createToken() {
		return new Token(token, secret);
	}
	
	public static OAuthToken createFromToken(Token token) {
		return new OAuthToken(token.getToken(), token.getSecret());
	}
}
