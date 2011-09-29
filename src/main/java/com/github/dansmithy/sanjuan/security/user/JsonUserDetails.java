package com.github.dansmithy.sanjuan.security.user;

import java.util.Arrays;

/**
 * This is a Java representation of a JSON user response.
 *
 *  This is used as the response body:
 *  a) On successful authentication,
 *  b) When requesting currently authenticated user
 */
public class JsonUserDetails {

	private String username;

	private String[] authorities;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String[] getAuthorities() {
		return Arrays.copyOf(authorities, authorities.length);
	}

	public void setAuthorities(String[] authorities) {
		this.authorities = Arrays.copyOf(authorities, authorities.length);
	}

}
