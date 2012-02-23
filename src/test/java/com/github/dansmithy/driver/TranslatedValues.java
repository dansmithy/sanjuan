package com.github.dansmithy.driver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class TranslatedValues {

	private Map<String, String> aliases = new HashMap<String, String>();
	
	public TranslatedValues() {
		super();
	}
	
	public TranslatedValues(Map<String, String> aliases) {
		super();
		this.aliases = aliases;
	}

	public RequestValues aliasRequestValues(RequestValues oldRequestValues) {
		RequestValues requestValues = new RequestValues();
		for (Map.Entry<String, String> entry : oldRequestValues.entrySet()) {
			if (entry.getValue().contains(",")) {
				handleArray(aliases, entry, requestValues);
			}
			else if (shouldHaveGeneratedAlias(entry.getValue())) {
				generateValueIfNotExistAndAdd(aliases, requestValues, entry);
			} else {
				requestValues.add(entry.getKey(), entry.getValue());
			}
		}
		return requestValues;
	}
	
	public String alias(String value) {
		return shouldHaveGeneratedAlias(value) ? addToMapIfNotExist(aliases, value) : value; 
	}
	
	private boolean shouldHaveGeneratedAlias(String value) {
		return value.startsWith("#");
	}
		
	private void handleArray(Map<String, String> map, Entry<String, String> entry,
			RequestValues requestValues) {
		String[] parts = entry.getValue().split(",");
		StringBuilder builder = new StringBuilder();
		String delimiter = "";
		for (String part : parts) {
			String value = part;
			if (value.startsWith("#")) {
				value = addToMapIfNotExist(aliases, value);
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
			String generatedValue = generateAlias(cleanValue);
			map.put(value, generatedValue);
			return generatedValue;
		} else {
			return map.get(value);
		}
	}	
	
	private static String generateAlias(String actualKey) {
		return String.format("%s-%s", actualKey, UUID.randomUUID().toString());
	}
	
	public String get(String key) {
		return aliases.get(key);
	}
	
	public boolean containsKey(String key) {
		return aliases.containsKey(key);
	}

	public TranslatedValues add(String key, String value) {
		aliases.put(key, value);
		return this;
	}
	
}
