package com.github.dansmithy.json;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class JsonStringMatcher extends TypeSafeMatcher<String> {

	private TypeSafeMatcher<JSON> jsonMatcher;
	
	public JsonStringMatcher(String expectedJson) {
		super();
		this.jsonMatcher = new JsonObjectMatcher(JSONSerializer.toJSON(expectedJson));
	}

	public JsonStringMatcher(String expectedJson, JsonTranslator translator) {
		this.jsonMatcher = new JsonObjectMatcher(JSONSerializer.toJSON(expectedJson), translator);
	}

	@Override
	public void describeTo(Description description) {
		jsonMatcher.describeTo(description);
	}


	@Override
	public boolean matchesSafely(String actual) {
		return jsonMatcher.matches(JSONSerializer.toJSON(actual));
	}

}
