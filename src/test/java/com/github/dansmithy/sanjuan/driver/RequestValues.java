package com.github.dansmithy.sanjuan.driver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

public class RequestValues {

	private Map<String, String> values = new HashMap<String, String>();
	
	public RequestValues add(String key, String value) {
		values.put(key, value);
		return this;
	}
	
	public RequestValues addAll(Map<String, String> extraValues) {
		values.putAll(extraValues);
		return this;
	}	

	public String toJson() {
		JSONObject obj = new JSONObject();
		obj.putAll(values);
		return obj.toString();
	}	

	public boolean containsKey(String key) {
		return values.containsKey(key);
	}

	public String get(String key) {
		return values.get(key);
	}
	
	public Set<Map.Entry<String, String>> entrySet() {
		return Collections.unmodifiableSet(values.entrySet());
	}

	public RequestValues addReadableData(String data) {
		return addAll(parse(data));
	}
	
	private Map<String, String> parse(String data) {
		Map<String, String> dataMap = new HashMap<String, String>();
		String[] pairs = data.split(", ");
		for (String pair : pairs) {
			if (!pair.contains(" : ")) {
				return dataMap;
			}
			String[] keyValue = pair.split(" : ");
			dataMap.put(keyValue[0], keyValue[1]);
		}
		return dataMap;
	}

	@Override
	public String toString() {
		return values.toString();
	}


}
