package com.github.dansmithy.sanjuan.dao.util;

import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.query.BasicUpdate;
import org.springframework.data.document.mongodb.query.Criteria;
import org.springframework.data.document.mongodb.query.Query;
import org.springframework.data.document.mongodb.query.Update;

import com.mongodb.BasicDBObject;

public class MongoHelper {

	public static Query createSimpleQuery(String propName, Object value) {
		return new Query(Criteria.where(propName).is(value));
	}
	
	public static Update createUpdate(MongoTemplate mongoTemplate, Object object) {
		BasicDBObject basicObject = new BasicDBObject();
		mongoTemplate.getConverter().write(object, basicObject);
		BasicDBObject updateObj = new BasicDBObject("$set", basicObject);
		return new BasicUpdate(updateObj);
		
	}
}
