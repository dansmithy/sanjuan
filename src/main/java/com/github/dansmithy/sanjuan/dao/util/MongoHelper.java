package com.github.dansmithy.sanjuan.dao.util;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
