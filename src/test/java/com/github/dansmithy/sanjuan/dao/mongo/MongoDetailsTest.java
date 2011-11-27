package com.github.dansmithy.sanjuan.dao.mongo;

import static org.hamcrest.Matchers.*;

import org.junit.Assert;
import org.junit.Test;

public class MongoDetailsTest {

	@Test
	public void testCreateFromUri() {
		
		MongoDetails details = MongoDetails.createFromUri("mongodb://crazy_username:longpassword@server.mongolab.com:27567/db_name");
		MongoDetails expectedDetails = new MongoDetails().withUsername("crazy_username").withPassword("longpassword").withHost("server.mongolab.com").withPort(27567).withDatabase("db_name");
		Assert.assertThat(details, is(equalTo(expectedDetails)));
	}
	
	@Test(expected = RuntimeException.class)
	public void testFailToCreateFromUri() {
		MongoDetails.createFromUri("wrong_format");
	}	
	
	@Test
	public void testCreateFromDefaultUri() {
		
		MongoDetails details = MongoDetails.createFromUri("mongodb://:@localhost:27017/test");
		MongoDetails expectedDetails = new MongoDetails().withUsername(null).withPassword(null).withHost("localhost").withPort(27017).withDatabase("test");
		Assert.assertThat(details, is(equalTo(expectedDetails)));
	}	
	
	@Test
	public void testCreateFromConstructor() {
		MongoDetails details = new MongoDetails("username", "password", "localhost", 27017, "test");
		MongoDetails expectedDetails = new MongoDetails().withUsername("username").withPassword("password").withHost("localhost").withPort(27017).withDatabase("test");
		Assert.assertThat(details, is(equalTo(expectedDetails)));
	}
	
	@Test
	public void testHashcode() {
		
		MongoDetails details1 = new MongoDetails("username", "password", "localhost", 27017, "test");
		MongoDetails details2 = new MongoDetails("username", "password", "localhost", 27017, "test");
		Assert.assertThat(details1.hashCode(), is(equalTo(details2.hashCode())));
	}
	
	@Test
	public void testToString() {
		
		MongoDetails details1 = new MongoDetails("username", "password", "localhost", 27017, "test");
		Assert.assertThat(details1.toString(), is(not(nullValue())));
	}		

	@Test
	public void testAutoMongoDetails() {
		
		AutoMongoDetails details = new AutoMongoDetails();
		MongoDetails expectedDetails = new MongoDetails().withUsername(null).withPassword(null).withHost("localhost").withPort(27017).withDatabase("test");
		Assert.assertThat(details, is(equalTo(expectedDetails)));
	}	

}
