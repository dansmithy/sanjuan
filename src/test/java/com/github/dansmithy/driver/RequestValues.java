package com.github.dansmithy.driver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class RequestValues implements Iterable<Map.Entry<String, String>> {

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
		for (Map.Entry<String, String> entry : this) {
			Object value = entry.getValue();
			if (entry.getValue().contains(",")) {
				value = JSONSerializer.toJSON(reformatArray(entry.getValue()));
			}
			obj.put(entry.getKey(), value);
		}
		return obj.toString();
	}	

	private String reformatArray(String value) {
		String textQualifier = "'";
		if (Character.isDigit(value.charAt(0))) {
			textQualifier = "";
		}
		return String.format("[ %s%s%s ]", textQualifier, value.replaceAll(",", String.format("%s,%s", textQualifier, textQualifier)), textQualifier);		
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
		String[] pairs = data.split("; ");
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

	@Override
	public Iterator<Entry<String, String>> iterator() {
		return values.entrySet().iterator();
	}


}
