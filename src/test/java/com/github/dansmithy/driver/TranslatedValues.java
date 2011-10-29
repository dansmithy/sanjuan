package com.github.dansmithy.driver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class TranslatedValues {

	private Map<String, String> translatedValues = new HashMap<String, String>();
	
	public TranslatedValues() {
		super();
	}
	
	public TranslatedValues(Map<String, String> translatedValues) {
		super();
		this.translatedValues = translatedValues;
	}

	public RequestValues translateRequestValues(RequestValues oldRequestValues) {
		RequestValues requestValues = new RequestValues();
		for (Map.Entry<String, String> entry : oldRequestValues.entrySet()) {
			if (entry.getValue().contains(",")) {
				handleArray(translatedValues, entry, requestValues);
			}
			else if (entry.getValue().startsWith("#")) {
				generateValueIfNotExistAndAdd(translatedValues, requestValues, entry);
			} else {
				requestValues.add(entry.getKey(), entry.getValue());
			}
		}
		return requestValues;
	}
	
	private void handleArray(Map<String, String> map, Entry<String, String> entry,
			RequestValues requestValues) {
		String[] parts = entry.getValue().split(",");
		StringBuilder builder = new StringBuilder();
		String delimiter = "";
		for (String part : parts) {
			String value = part;
			if (value.startsWith("#")) {
				value = addToMapIfNotExist(translatedValues, value);
			}
			builder.append(delimiter);
			builder.append(value);
			delimiter = ",";
		}
		requestValues.add(entry.getKey(), builder.toString());
	}

	private static void generateValueIfNotExistAndAdd(Map<String, String> map,
			RequestValues requestValues, Entry<String, String> entry) {
		
		String generatedValue = addToMapIfNotExist(map, entry.getValue());
		if (!requestValues.containsKey(entry.getKey())){
			requestValues.add(entry.getKey(), generatedValue);
		}
	}	
	
	private static String addToMapIfNotExist(Map<String, String> map,
			String value) {
		if (!map.containsKey(value)) {
			String cleanValue = value.substring(1);
			String generatedValue = generateValue(cleanValue);
			map.put(value, generatedValue);
			return generatedValue;
		} else {
			return map.get(value);
		}
	}	
	
	private static String generateValue(String actualKey) {
		return String.format("%s-%s", actualKey, UUID.randomUUID().toString());
	}
	
	public String get(String key) {
		return translatedValues.get(key);
	}
	
	public boolean containsKey(String key) {
		return translatedValues.containsKey(key);
	}

	public TranslatedValues add(String key, String value) {
		translatedValues.put(key, value);
		return this;
	}
	
}
