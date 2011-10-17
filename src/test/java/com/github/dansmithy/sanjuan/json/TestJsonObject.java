package com.github.dansmithy.sanjuan.json;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import org.junit.Before;
import org.junit.Test;

public class TestJsonObject {

	private JsonStringCompare jsonStringCompare;

	@Before
	public void setup() {
		jsonStringCompare = new JsonStringCompare(new JsonCompare());
	}
	@Test
	public void testJsonObject() {
		
		
		String json = "[{ 'simple' : true }]";
		
		JSON jo = JSONSerializer.toJSON(json);
		System.out.println(jo);
		
		if (jo.isArray()) {
			System.out.println("Array");
		} else {
			System.out.println("Not array");
		}
		
	}
	
	@Test
	public void testMyCompare1() {
		String actual = "[{ 'simple' : null}]";
		String expected = "[{ 'simple' : null}]";
		jsonStringCompare.equalsNoOrphans(actual, expected);
	}
	
	@Test
	public void testMyCompare2() {
		String actual = "[{ 'simple' : {}}]";
		String expected = "[{ 'simple' : {}}]";
		jsonStringCompare.equalsNoOrphans(actual, expected);
	}	
	
	@Test
	public void testMyCompare3() {
		String actual = "[{ 'simple' : true}]";
		String expected = "[{ 'simple' : true}]";
		jsonStringCompare.equalsNoOrphans(actual, expected);
	}		
	
	@Test
	public void testMyCompare4() {
		String actual = "[{ 'simple' : { 'deeper' : 'underground' }}]";
		String expected = "[{ 'simple' : { 'deeper' : 'love' }}]";
		jsonStringCompare.equalsNoOrphans(actual, expected);
	}	
	
	@Test
	public void testMyCompare5() {
		String actual = "{ 'items' : [{ 'name' : 'one', 'type' : 'food' }, { 'name' : 'two', 'type' : 'clothes'}]}";
		String expected = "{ 'items^name' : [{ 'name' : 'two', 'type' : 'clothes' }]}";
		jsonStringCompare.equalsNoOrphans(actual, expected);
	}		
	
}
