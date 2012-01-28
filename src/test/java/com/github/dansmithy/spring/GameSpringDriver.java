package com.github.dansmithy.spring;

import java.util.HashMap;
import java.util.Map;

import com.github.dansmithy.driver.GameDriverSession;
import com.github.dansmithy.driver.RequestValues;
import com.github.dansmithy.driver.SkeletonGameDriver;
import com.github.dansmithy.sanjuan.model.User;
import com.github.dansmithy.sanjuan.rest.jaxrs.CardResource;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameResource;
import com.github.dansmithy.sanjuan.rest.jaxrs.UserResource;
import com.github.dansmithy.sanjuan.security.SecurityContextAuthenticatedSessionProvider;
import com.github.restdriver.serverdriver.http.response.Response;

public class GameSpringDriver extends SkeletonGameDriver {

	private GameResource gameResource;
	private UserResource userResource;
	private final CardResource cardResource;
	private SecurityContextAuthenticatedSessionProvider sessionProvider;
	
	private Map<String, User> users = new HashMap<String, User>();
	
	public GameSpringDriver(GameResource gameResource,
			UserResource userResource, CardResource cardResource, SecurityContextAuthenticatedSessionProvider sessionProvider, String adminUsername, String adminPassword) {
		super(adminUsername, adminPassword);
		this.gameResource = gameResource;
		this.userResource = userResource;
		this.cardResource = cardResource;
		this.sessionProvider = sessionProvider;
		createAdminSession();
	}

	@Override
	protected GameDriverSession login(String username, String password, boolean isAdmin) {
		RequestValues requestValues = createTranslatedUserRequest(username, password);
		User user = new User();
		user.setPassword(requestValues.get("password"));
		user.setUsername(requestValues.get("username"));
		String[] roles = isAdmin ? new String[] { "player", "admin" } : new String[] { "player" };
		user.setRoles(roles);
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

	@Override
	public Response getCards() {
		return new SpringResponse(cardResource.getCards());
	}
	
	@Override
	public Response getCardTypes() {
		return new SpringResponse(cardResource.getCardTypes());
	}
	
	
	

}
