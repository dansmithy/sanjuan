package com.github.dansmithy.spring;

import java.util.HashMap;
import java.util.Map;

import com.github.dansmithy.driver.GameDriverSession;
import com.github.dansmithy.driver.RequestValues;
import com.github.dansmithy.driver.SkeletonGameDriver;
import com.github.dansmithy.sanjuan.model.User;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameResource;
import com.github.dansmithy.sanjuan.rest.jaxrs.UserResource;
import com.github.dansmithy.sanjuan.security.SecurityContextAuthenticatedSessionProvider;

public class GameSpringDriver extends SkeletonGameDriver {

	private GameResource gameResource;
	private UserResource userResource;
	private SecurityContextAuthenticatedSessionProvider sessionProvider;
	
	private Map<String, User> users = new HashMap<String, User>();
	
	public GameSpringDriver(GameResource gameResource,
			UserResource userResource, SecurityContextAuthenticatedSessionProvider sessionProvider, String adminUsername, String adminPassword) {
		super(adminUsername, adminPassword);
		this.gameResource = gameResource;
		this.userResource = userResource;
		this.sessionProvider = sessionProvider;
		createAdminSession();
	}

	@Override
	protected GameDriverSession login(String username, String password) {
		RequestValues requestValues = createTranslatedUserRequest(username, password);
		User user = new User();
		user.setPassword(requestValues.get("password"));
		user.setUsername(requestValues.get("username"));
		user.setRoles(new String[] { "player", "admin" });
		sessionProvider.addUser(user);
		users.put(username, user);
		return new GameSpringDriverSession(getTranslatedValues(), gameResource, userResource);
	}

	@Override
	public GameDriverSession getSession(String username) {
		User user = users.get(username);
		sessionProvider.addUser(user);
		return super.getSession(username);
	}
	
	
	
	

}
