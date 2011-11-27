package com.github.dansmithy.sanjuan.dao.util;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoHelper {

	MongoHelper() { }
	
	public static Query createSimpleQuery(String propName, Object value) {
		return new Query(Criteria.where(propName).is(value));
	}

}
