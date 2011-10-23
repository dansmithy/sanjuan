package com.github.dansmithy.sanjuan.driver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public class TranslatedValues {

	private Map<String, String> translatedValues = new HashMap<String, String>();
	
	public RequestValues translateRequestValues(RequestValues oldRequestValues) {
		RequestValues requestValues = new RequestValues();
		for (Map.Entry<String, String> entry : oldRequestValues.entrySet()) {
			if (entry.getValue().startsWith("#")) {
				generateValueIfNotExist(translatedValues, requestValues, entry);
			} else {
				requestValues.add(entry.getKey(), entry.getValue());
			}
		}
		return requestValues;
	}
	
	private static void generateValueIfNotExist(Map<String, String> map,
			RequestValues requestValues, Entry<String, String> entry) {
		if (!map.containsKey(entry.getValue())) {
			String cleanValue = entry.getValue().substring(1);
			String generatedValue = generateValue(cleanValue);
			map.put(entry.getValue(), generatedValue);
			requestValues.add(entry.getKey(), generatedValue);
		} else if (!requestValues.containsKey(entry.getKey())){
			requestValues.add(entry.getKey(), map.get(entry.getValue()));
		}
	}	
	
	private static String generateValue(String actualKey) {
		return String.format("%s-%s", actualKey, UUID.randomUUID().toString());
	}

}
