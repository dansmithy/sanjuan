package com.github.dansmithy.sanjuan.security.user;

public class AuthenticatedUser {

	private static final ThreadLocal<String> userThreadLocal = new ThreadLocal<String>();

	public static void set(String user) {
		userThreadLocal.set(user);
	}

	public static void unset() {
		userThreadLocal.remove();
	}

	public static String get() {
		return userThreadLocal.get();
	}
}
