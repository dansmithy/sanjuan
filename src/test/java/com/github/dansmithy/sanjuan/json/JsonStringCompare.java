package com.github.dansmithy.sanjuan.json;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

public class JsonStringCompare implements TestCompare<String, String> {

	private final TestCompare<JSON, JSON> jsonCompare;
	
	public JsonStringCompare(TestCompare<JSON, JSON> jsonCompare) {
		this.jsonCompare = jsonCompare;
		
	}
	@Override
	public void equalsNoOrphans(String actual, String expected) {
		
		jsonCompare.equalsNoOrphans(JSONSerializer.toJSON(actual), JSONSerializer.toJSON(expected));
	}
	

}
