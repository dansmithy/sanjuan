package com.github.dansmithy.spring;

import java.util.HashMap;
import java.util.Map;

import com.github.dansmithy.driver.GameDriverSession;
import com.github.dansmithy.driver.RequestValues;
import com.github.dansmithy.driver.SkeletonGameDriver;
import com.github.dansmithy.sanjuan.rest.jaxrs.CardResource;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameResource;
import com.github.dansmithy.sanjuan.rest.jaxrs.UserResource;
import com.github.dansmithy.sanjuan.twitter.model.TwitterUser;
import com.github.dansmithy.sanjuan.twitter.service.TwitterUserStore;
import com.github.restdriver.serverdriver.http.response.Response;

public class GameSpringDriver extends SkeletonGameDriver {

	private GameResource gameResource;
	private UserResource userResource;
	private final CardResource cardResource;
	private TwitterUserStore sessionProvider;
	
	private Map<String, TwitterUser> users = new HashMap<String, TwitterUser>();
	
	public GameSpringDriver(GameResource gameResource,
			UserResource userResource, CardResource cardResource, TwitterUserStore sessionProvider, String adminUsername, String adminPassword) {
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
		String[] roles = isAdmin ? new String[] { "player", "admin" } : new String[] { "player" };
		TwitterUser user = new TwitterUser(requestValues.get("username"), null, roles);
		sessionProvider.setCurrentUser(user);
		users.put(username, user);
		return new GameSpringDriverSession(getTranslatedValues(), gameResource, userResource);
	}

	@Override
	public GameDriverSession getSession(String username) {
		TwitterUser user = users.get(username);
		sessionProvider.setCurrentUser(user);
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
