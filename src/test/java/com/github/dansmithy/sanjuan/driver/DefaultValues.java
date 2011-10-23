package com.github.dansmithy.sanjuan.driver;

import java.util.Map;

public class DefaultValues {

	public static final Map<String, String> USER = createUserDefaults();

	private static Map<String, String> createUserDefaults() {
		return MapBuilder.simple().add("password", "testPassword").buildUnmodifiable();
	}
	
}
