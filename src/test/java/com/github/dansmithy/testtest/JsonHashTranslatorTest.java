package com.github.dansmithy.testtest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import org.junit.Assert;
import org.junit.Test;

import com.github.dansmithy.driver.TranslatedValues;
import com.github.dansmithy.json.JsonHashTranslator;
import com.github.dansmithy.util.MapBuilder;

public class JsonHashTranslatorTest {
	
	
	@Test
	public void testReplaceIntegerValueAsObjectValue() {
		TranslatedValues translatedValues = new TranslatedValues(MapBuilder.simple().add("#count", "20").build());
		JsonHashTranslator translator = new JsonHashTranslator(translatedValues);
		
		JSON actual = translator.translate(JSONSerializer.toJSON("{ 'count' : '#count' }"));
		JSON expected = JSONSerializer.toJSON("{ 'count' : 20 }");
		
		Assert.assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void testReplaceIntegerValueAsArrayValue() {
		TranslatedValues translatedValues = new TranslatedValues(MapBuilder.simple().add("#count", "20").build());
		JsonHashTranslator translator = new JsonHashTranslator(translatedValues);
		
		JSON actual = translator.translate(JSONSerializer.toJSON("{ 'numbers' : [ '#count' ] }"));
		JSON expected = JSONSerializer.toJSON("{ 'numbers' : [ 20 ] }");
		
		Assert.assertThat(actual, is(equalTo(expected)));
	}
	
	@Test
	public void testReplaceMultipleIntegerValuesAsArrayValues() {
		TranslatedValues translatedValues = new TranslatedValues(MapBuilder.simple().add("#a", "1").add("#b", "2").add("#c", "3").build());
		JsonHashTranslator translator = new JsonHashTranslator(translatedValues);
		
		JSON actual = translator.translate(JSONSerializer.toJSON("{ 'numbers' : [ '#a', '#b', '#c' ] }"));
		JSON expected = JSONSerializer.toJSON("{ 'numbers' : [ 1, 2, 3 ] }");
		
		Assert.assertThat(actual, is(equalTo(expected)));
	}	
}
