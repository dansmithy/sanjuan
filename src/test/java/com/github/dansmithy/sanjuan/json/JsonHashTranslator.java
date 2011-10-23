package com.github.dansmithy.sanjuan.json;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.github.dansmithy.sanjuan.driver.TranslatedValues;

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
			translateJsonObj(array.get(count));
		}
	}

	private boolean translateJsonObj(Object object) {
		if (object instanceof JSON) {
			JSON json = (JSON)object;
			if (json.isArray()) {
				translateArray((JSONArray)json);
				return false;
			} else {
				translateJSONObject((JSONObject)json);
				return false;
			}
		} else if (object instanceof String) {
			if (((String)object).startsWith("#")) {
				return true;
			}
		}
		return false;

	}

	private void translateJSONObject(JSONObject json) {
		for (Object key : json.keySet()) {
			if (translateJsonObj(json.get(key))) {
				String value = ((String)json.get(key));
				if (!translatedValues.containsKey(value)) {
					throw new RuntimeException(String.format("Cannot find %s as a translated value.", value));
				}
				json.put(key, translatedValues.get(value));
			}
		}
	}

}
