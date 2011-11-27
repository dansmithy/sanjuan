package com.github.dansmithy.sanjuan.dao;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class SequenceMongoIdGeneratorTest {

	@Test
	public void testCreateNewSequence() {
		
		// given 
		MongoTemplate mockMongoTemplate = mock(MongoTemplate.class);
		DBCollection mockDbCollection = mock(DBCollection.class);
		DBObject mockDbObject = mock(DBObject.class);
		when(mockMongoTemplate.getCollection(anyString())).thenReturn(mockDbCollection);
		when(mockDbCollection.findAndModify(org.mockito.Matchers.any(DBObject.class), org.mockito.Matchers.any(DBObject.class))).thenReturn(null, mockDbObject);
		when(mockDbObject.get(anyString())).thenReturn(new Long(5));
		SequenceMongoIdGenerator sequenceMongoIdGenerator = new SequenceMongoIdGenerator(mockMongoTemplate);
		
		// when
		Long actualId = sequenceMongoIdGenerator.getNextLongId("typeName");
		
		// then
		Assert.assertThat(actualId, is(equalTo(new Long(5))));
	}
}
