package com.github.dansmithy.sanjuan.json;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;

public class JsonObjectMatcher extends TypeSafeMatcher<JSON> {

	private final JSON expected;
	private String descriptionText;

	public JsonObjectMatcher(JSON expected) {
		super();
		this.expected = expected;
	}

	public JsonObjectMatcher(JSON expected, JsonTranslator translator) {
		this.expected = translator.translate(expected);
	}
	
	@Override
	public boolean matchesSafely(JSON actual) {
		try {
			compare(actual, expected, "", ArrayContext.NULL);
			return true;
		} catch (AssertionError e) {
			descriptionText = e.getMessage();
			return false;
		}
	}	

	@Override
	public void describeTo(Description description) {
		description.appendText(descriptionText);
		
	}
	
	private void compareJSON(JSON actual, JSON expected, String expectedPath, ArrayContext context) {
		if (actual.isArray()) {
			compareArrays((JSONArray)actual, (JSONArray)expected, expectedPath, context);
		} else if (actual instanceof JSONNull){
			// do nothing. Are both null
		} else {
			Assert.assertTrue(String.format("Do not use the ^ character in the path %s unless it is an array value", expectedPath), context.isNull());
			compareObjects((JSONObject)actual, (JSONObject)expected, expectedPath);
		}
	}

	private void compareObjects(JSONObject actual, JSONObject expected, String expectedPath) {
		
		for (Object expectedKeyObject : expected.keySet()) {
			String expectedKey = (String)expectedKeyObject;
			ArrayContext context = ArrayContext.NULL; 
			if (expectedKey.indexOf("^") != -1) {
				context = new ArrayContext(expectedKey.substring(expectedKey.indexOf("^")+1));
				expectedKey = expectedKey.substring(0, expectedKey.indexOf("^"));
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
			Assert.assertTrue(String.format("Do not use the ^ character in the path %s unless it is an array value", expectedPath), context.isNull());
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
	}
	
	private void compareArraysContainValues(JSONArray actual,
			JSONArray expected, String expectedPath, ArrayContext context) {
		Assert.assertThat(String.format("Arrays at path %s needs to have at least as many elements as you want to match.", expectedPath), actual.size(), is(greaterThanOrEqualTo(expected.size())));
		for (int index = 0; index < expected.size(); index++) {
			Object expectedItem = (Object)expected.get(index);
			Assert.assertFalse("You cannot use the ^ char with an array value", expectedItem instanceof JSONArray);
			Assert.assertFalse("You cannot use the ^ char with a null value", expectedItem instanceof JSONNull);
			ArrayItemMatcher matcher = new ArrayItemMatcher(expectedItem, context);
			ArrayItemMatchResult result = matcher.matchInArray(actual);
			Assert.assertTrue(String.format("Not found item with %s['%s']=%s", expectedPath, context.getIdentifierField(), result.getFieldValue()), result.exists());
			compare(result.getItem(), expectedItem, String.format("%s['%s'=%s]", expectedPath, context.getIdentifierField(), result.getFieldValue()), ArrayContext.NULL);
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

		public ArrayContext(String identifierField) {
			super();
			this.identifierField = identifierField;
		}
		public String getIdentifierField() {
			return identifierField;
		}
		
		
		public boolean isNull() {
			return identifierField == null;
		}
		
		public boolean identifiesPrimitive() {
			return !isNull() && "".equals(identifierField);
		}

		public boolean identifiesObject() {
			return !isNull() && !identifiesPrimitive();
		}

	}
	
	private static class ArrayItemMatcher {

		private final Object expectedItem;
		private final ArrayContext context;
		private boolean isObject;

		public ArrayItemMatcher(Object expectedItem, ArrayContext context) {
			this.expectedItem = expectedItem;
			this.context = context;
			this.isObject = expectedItem instanceof JSONObject;
			Assert.assertFalse("Need to have an array of objects if you want to use an identifier field", isObject && context.identifiesPrimitive());
			Assert.assertFalse("Can only specify identifier field with array of objects", !isObject && context.identifiesObject());
			if (isObject) {
				Assert.assertNotNull(String.format("Specify identifier field, expected item must have field '%s'.", context.getIdentifierField()), getPrimitive(expectedItem));
			}
		}

		public ArrayItemMatchResult matchInArray(JSONArray array) {
			Object found = null;
			for (int index = 0; index < array.size(); index++) {
				Object item = (Object)array.get(index);
				if (checkPrimitiveEquals(getPrimitive(expectedItem), getPrimitive(item))) {
					found = item;
					break;
				}
			}
			return new ArrayItemMatchResult(found != null, found, getPrimitive(expectedItem).toString());
		}

		private <T> boolean checkPrimitiveEquals(T expected, T actual) {
			return expected.equals(actual);
		}

		private Object getPrimitive(Object item) {
			return isObject ? asObject(item).get(context.getIdentifierField()) : item;
		}

		private JSONObject asObject(Object object) {
			return (JSONObject)object;
		}
		
	}	
	
	private static class ArrayItemMatchResult {

		private boolean exists;
		private Object item;
		private String fieldValue;
		
		public ArrayItemMatchResult(boolean exists, Object item,
				String fieldValue) {
			super();
			this.exists = exists;
			this.item = item;
			this.fieldValue = fieldValue;
		}

		public boolean exists() {
			return exists;
		}

		public Object getItem() {
			return item;
		}

		public String getFieldValue() {
			return fieldValue;
		}
		
		
	}	

}
