package com.github.dansmithy.sanjuan.dao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

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
	
	public List<User> getUsers() {
		return mongoTemplate.findAll(User.class);
	}
	
	public void createUser(User user) {
		mongoTemplate.insert(user);
	}
	
	public void updateUser(User userUpdate) {
		User currentUser = getUser(userUpdate.getUsername());
		currentUser.setHashedPassword(userUpdate.getHashedPassword());
		currentUser.setRoles(userUpdate.getRoles());
		mongoTemplate.save(currentUser);
	}
	
	public void removeUser(String username) {
		Query query = MongoHelper.createSimpleQuery("username", username);	
		mongoTemplate.remove(query, User.class);
	}
	
}
