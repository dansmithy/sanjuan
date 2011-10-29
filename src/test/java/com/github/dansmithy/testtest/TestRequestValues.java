package com.github.dansmithy.testtest;

import org.junit.Test;

import com.github.dansmithy.driver.RequestValues;
import com.github.dansmithy.driver.TranslatedValues;

public class TestRequestValues {

	@Test
	public void testArrayValue() {
		
		String input = "build : #coffeeroaster; payment : #one,#two,#three";
		RequestValues rv = new RequestValues();
		rv.addReadableData(input);
		
		System.out.println(rv.toJson());
	}
	
	@Test
	public void testArrayValueTranslated() {
		
		TranslatedValues tv = new TranslatedValues().add("#one", "first").add("#two", "second").add("#three", "third").add("#coffeeroaster", "40");
		String input = "build : #coffeeroaster; payment : #one,#two,#three";
		RequestValues rv = new RequestValues();
		rv.addReadableData(input);
		RequestValues newRV = tv.translateRequestValues(rv);
		
		
		System.out.println(newRV.toJson());
	}	
	
	@Test
	public void testArrayValueIntegersTranslated() {
		
		TranslatedValues tv = new TranslatedValues().add("#one", "1").add("#two", "2").add("#three", "3").add("#coffeeroaster", "40");
		String input = "build : #coffeeroaster; payment : #one,#two,#three";
		RequestValues rv = new RequestValues();
		rv.addReadableData(input);
		RequestValues newRV = tv.translateRequestValues(rv);
		
		
		System.out.println(newRV.toJson());
	}	
	

}
