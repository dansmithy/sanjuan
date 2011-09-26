package com.github.dansmithy.sanjuan.dao;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.query.Criteria;
import org.springframework.data.document.mongodb.query.Query;
import org.springframework.data.document.mongodb.query.Update;

import com.mongodb.DBObject;

@Named
public class SequenceMongoIdGenerator implements MongoIdGenerator {
	
	private final MongoTemplate mongoTemplate;

	@Inject
	public SequenceMongoIdGenerator(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public Long getNextLongId(String type) {
		Update update = new Update().inc("seq", 1);
		DBObject idObject = mongoTemplate.getCollection("seq").findAndModify(createSimpleQuery("_id", type).getQueryObject(), update.getUpdateObject());
		if (idObject == null) {
			createFirstLong(type);
			return getNextLongId(type);
		} else {
			return (Long)idObject.get("seq");
		}
	}
	
	private void createFirstLong(String type) {
		mongoTemplate.insert(new MongoSequence(type, new Long(1)));
		
	}

	private static <T> Query createSimpleQuery(String propName, T value) {
		return new Query(Criteria.where(propName).is(value));
	}	

}
