package com.github.dansmithy.driver;

import java.util.Map;

import com.github.dansmithy.util.MapBuilder;


public class DefaultValues {

	public static final String PASSWORD = "testPassword";
	
	public static Boolean HTTP_MODE = Boolean.TRUE;
	public static String ADMIN_USERNAME = "danny";
	public static String ADMIN_PASSWORD = "danny";
	public static String BASE_URI = "http://localhost:8086";

	public static final Map<String, String> USER = createUserDefaults();

	
	private static Map<String, String> createUserDefaults() {
		return MapBuilder.simple().add("password", PASSWORD).buildUnmodifiable();
	}
	
}
