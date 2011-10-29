package com.github.dansmithy.spring;

import javax.ws.rs.core.Response;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.SkeletonBddTestRunner;
import com.github.dansmithy.driver.GameDriver;
import com.github.dansmithy.sanjuan.exception.RestExceptionMapper;
import com.github.dansmithy.sanjuan.exception.SanJuanException;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameResource;
import com.github.dansmithy.sanjuan.rest.jaxrs.UserResource;
import com.github.dansmithy.sanjuan.security.SecurityContextAuthenticatedSessionProvider;

public class BddSpringTestRunner extends SkeletonBddTestRunner<GameDriver> {

	private static final RestExceptionMapper EXCEPTION_MAPPER = new RestExceptionMapper();
	
	private final GameResource gameResource;
	private final UserResource userResource;
	private SecurityContextAuthenticatedSessionProvider sessionProvider;
	private final String adminUsername;
	private final String adminPassword;
	

	public BddSpringTestRunner(String adminUsername, String adminPassword) {
		this.adminUsername = adminUsername;
		this.adminPassword = adminPassword;
		ApplicationContext context = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/applicationContext.xml");
		gameResource = context.getBean(GameResource.class);
		userResource = context.getBean(UserResource.class);
		sessionProvider = context.getBean(SecurityContextAuthenticatedSessionProvider.class);
	}

	@Override
	protected GameDriver createContext() {
		return new GameSpringDriver(gameResource, userResource, sessionProvider, adminUsername, adminPassword);
	}

	public void afterTest(GameDriver context) {
		context.cleanup();
	}

	@Override
	protected void doEvent(BddPart<GameDriver> given, GameDriver context) {
		try {
			super.doGiven(given, context);
		} catch (SanJuanException e) {
			Response response = EXCEPTION_MAPPER.toResponse(e);
			context.setLastResponse(new SpringResponse(response.getEntity(), response.getStatus())); 
		}
	}
	
	

}
