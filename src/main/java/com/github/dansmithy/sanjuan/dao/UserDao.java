package com.github.dansmithy.sanjuan.dao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.model.Game;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.github.dansmithy.sanjuan.dao.util.MongoHelper;
import com.github.dansmithy.sanjuan.model.User;
import org.springframework.data.mongodb.core.query.Update;

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

    public void recordLogin(String username) {
        
        User user = getUser(username);
        if (null == user) {
            user = new User();
            user.setUsername(username);
            Date loginDate = new Date();
            user.setFirstLogin(loginDate);
            user.setLastLogin(loginDate);
            user.setTimesLoggedIn(1L);
            mongoTemplate.save(user);
        } else {
            Update update = new Update();
            update.set("lastLogin", new Date());
            update.inc("timesLoggedIn", 1);
            mongoTemplate.updateFirst(MongoHelper.createSimpleQuery("username", username), update, User.class);            
        }
    }

	public void removeUser(String username) {
		Query query = MongoHelper.createSimpleQuery("username", username);	
		mongoTemplate.remove(query, User.class);
	}
	
}
