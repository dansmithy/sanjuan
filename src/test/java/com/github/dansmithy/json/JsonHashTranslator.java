package com.github.dansmithy.json;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.github.dansmithy.driver.TranslatedValues;

public class JsonHashTranslator implements JsonTranslator {

	private final TranslatedValues translatedValues;

	public JsonHashTranslator(TranslatedValues translatedValues) {
		this.translatedValues = translatedValues;
	}

	@Override
	public JSON translate(JSON input) {
		translateJsonObj(input);
		return input;
	}

	private void translateArray(JSONArray array) {
		for (int count = 0; count < array.size(); count++) {
			Object replacement = translateJsonObj(array.get(count));
			if (replacement != null) {
				array.remove(count);
				array.add(count, replacement);
			}
		}
	}

	private Object translateJsonObj(Object object) {
		if (object instanceof JSON) {
			JSON json = (JSON)object;
			if (json.isArray()) {
				translateArray((JSONArray)json);
				return null;
			} else {
				translateJSONObject((JSONObject)json);
				return null;
			}
		} else if (object instanceof String) {
			if (((String)object).startsWith("#")) {
				String stringValue = (String)object;
				return createTranslatedObject(stringValue);
			}
		}
		return null;
	}
	
	private Object createTranslatedObject(String stringValue) {
		if (!translatedValues.containsKey(stringValue)) {
			throw new RuntimeException(String.format("Cannot find %s as a translated value.", stringValue));
		}
		stringValue = translatedValues.get(stringValue);
		Object value = stringValue;
		if (Character.isDigit(stringValue.charAt(0))) {
			value = new Long(stringValue);
		}
		return value;
	}

	private void translateJSONObject(JSONObject json) {
		for (Object key : json.keySet()) {
			Object replacement = translateJsonObj(json.get(key));
			if (replacement != null) {
				json.put(key, replacement);
			}
		}
	}

}
