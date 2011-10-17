package com.github.dansmithy.sanjuan.json;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.junit.Assert;

public class JsonCompare implements TestCompare<JSON, JSON> {

	public void equalsNoOrphans(JSON actual, JSON expected) {
		compare(actual, expected, "", ArrayContext.NULL);
	}
	
	private void compareJSON(JSON actual, JSON expected, String expectedPath, ArrayContext context) {
		if (actual.isArray()) {
			compareArrays((JSONArray)actual, (JSONArray)expected, expectedPath, context);
		} else if (actual instanceof JSONNull){
			// do nothing. Are both null
		} else {
			Assert.assertFalse(String.format("Do not use the ^ character in the path %s unless it is an array value", expectedPath), context.isNull());
			compareObjects((JSONObject)actual, (JSONObject)expected, expectedPath);
		}
	}

	private void compareObjects(JSONObject actual, JSONObject expected, String expectedPath) {
		
		for (Object expectedKeyObject : expected.keySet()) {
			String expectedKey = (String)expectedKeyObject;
			ArrayContext context = ArrayContext.NULL; 
			if (expectedKey.indexOf("^") != -1) {
				expectedKey = expectedKey.substring(0, expectedKey.indexOf("^"));
				context = new ArrayContext(expectedKey.substring(expectedKey.indexOf("^")+1));
			}
			String newExpectedPath = expectedPath + "." + expectedKey;
			Assert.assertTrue(String.format("Expected %s to exist!", newExpectedPath), actual.containsKey(expectedKey));
			compare(actual.get(expectedKey), expected.get(expectedKeyObject), newExpectedPath, context);
		}
	}

	private void compare(Object actual, Object expected, String expectedPath, ArrayContext context) {
		Assert.assertThat(String.format("Expected %s to be same type", expectedPath), actual.getClass().getName(), is(equalTo(expected.getClass().getName())));
		if (actual instanceof JSON) {
			compareJSON((JSON)actual, (JSON)expected, expectedPath, context);
		} else {
			Assert.assertFalse(String.format("Do not use the ^ character in the path %s unless it is an array value", expectedPath), context.isNull());
			comparePrimitive(actual, expected, expectedPath);
		}
	}
	
	private <T> void comparePrimitive(T actual, T expected,
			String expectedPath) {
		Assert.assertThat(String.format("Values at %s not equal", expectedPath), actual, is(equalTo(expected)));
	}

	private void compareArrays(JSONArray actual, JSONArray expected, String expectedPath, ArrayContext context) {
		if (context.isNull()) {
			compareArraysExactly(actual, expected, expectedPath);
		} else {
			compareArraysContainValues(actual, expected, expectedPath, context);
		}
		for (int index = 0; index < expected.size(); index++) {
			Object expectedItem = (Object)expected.get(index);
			Object actualItem = (Object)actual.get(index);
			compare(actualItem, expectedItem, expectedPath + "[" + index + "]", ArrayContext.NULL);
		}
	}
	
	private void compareArraysContainValues(JSONArray actual,
			JSONArray expected, String expectedPath, ArrayContext context) {
		Assert.assertThat(String.format("Arrays at path %s needs to have at least as many elements as you want to match.", expectedPath), actual.size(), is(greaterThanOrEqualTo(expected.size())));
		for (int index = 0; index < expected.size(); index++) {
			Object expectedItem = (Object)expected.get(index);
			Assert.assertFalse("You cannot use the ^ char with an array value", expectedItem instanceof JSONArray);
			ArrayItemMatcher matcher = new ArrayItemMatcher(expectedItem, context);
			// TODO see if item exists and matches.
		}
	}

	private void compareArraysExactly(JSONArray actual, JSONArray expected,
			String expectedPath) {
		Assert.assertThat(String.format("Both arrays at path %s need to be of same size", expectedPath), actual.size(), is(equalTo(expected.size())));
		for (int index = 0; index < expected.size(); index++) {
			Object expectedItem = (Object)expected.get(index);
			Object actualItem = (Object)actual.get(index);
			compare(actualItem, expectedItem, expectedPath + "[" + index + "]", ArrayContext.NULL);
		}
		
	}

	private static class ArrayContext {
		private String identifierField;
		
		private static final ArrayContext NULL = new ArrayContext(null);

		public String getIdentifierField() {
			return identifierField;
		}

		public ArrayContext(String identifierField) {
			super();
			this.identifierField = identifierField;
		}
		
		public boolean isNull() {
			return identifierField == null;
		}
		
		public boolean identifiesPrimitive() {
			return !isNull() && "".equals(identifierField);
		}
		
	}
	
	private static class ArrayItemMatcher {

		public ArrayItemMatcher(Object expectedItem, ArrayContext context) {
			if (context.identifiesPrimitive()) {
				//.expectedItem instanceof JSONObject
			}
		}
		
		
	}	
}