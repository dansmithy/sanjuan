package com.github.dansmithy.json;

import net.sf.json.JSON;

import org.hamcrest.TypeSafeMatcher;

import com.github.dansmithy.driver.TranslatedValues;

public class JsonMatchers {

	public static TypeSafeMatcher<String> containsJson(String expected) {
		return new JsonStringMatcher(expected);
	}
	
	public static TypeSafeMatcher<String> containsJson(String expected, JsonTranslator translator) {
		return new JsonStringMatcher(expected, translator);
	}	
	
	public static TypeSafeMatcher<JSON> containsJson(JSON expected) {
		return new JsonObjectMatcher(expected);
	}

	public static TypeSafeMatcher<JSON> containsJson(JSON expected, JsonTranslator translator) {
		return new JsonObjectMatcher(expected, translator);
	}

	public static JsonTranslator whenTranslatedBy(TranslatedValues translatedValues) {
		return new JsonHashTranslator(translatedValues);
	}		
	
}
