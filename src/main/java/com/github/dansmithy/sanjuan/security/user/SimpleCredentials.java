package com.github.dansmithy.sanjuan.security.user;

/**
 * This is the Java representation of the JSON body for authentication requests.
 *
 * An attempt to authenticate will look like this:
 *
 * {
 *   "username" : "chuck",
 *   "password" : "n0rr1s"
 * }
 */
public class SimpleCredentials {

	public static final SimpleCredentials EMPTY_CREDENTIALS = new SimpleCredentials();

	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SimpleCredentials withUsername(String username) {
		setUsername(username);
		return this;
	}

	public SimpleCredentials withPassword(String password) {
		setPassword(password);
		return this;
	}

}
