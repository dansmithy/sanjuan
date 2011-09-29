package com.github.dansmithy.sanjuan.dao;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.query.Query;

import com.github.dansmithy.sanjuan.dao.util.MongoHelper;
import com.github.dansmithy.sanjuan.model.User;

@Named
public class UserDao {

	private final MongoTemplate mongoTemplate;

	@Inject
	public UserDao(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}
	
	public User getUser(String username) {
		Query query = MongoHelper.createSimpleQuery("username", username);		
		return mongoTemplate.findOne(query, User.class);		
	}
	
	public User addUser(User user) {
		return null;
	}
	
	public String encrypt(String password) {
		return DigestUtils.md5Hex(password);
	}	
	
	public static void main(String[] args) {
		System.out.println(new UserDao(null).encrypt("isabelle"));
	}
	
}
