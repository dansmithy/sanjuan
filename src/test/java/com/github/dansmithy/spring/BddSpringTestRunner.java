package com.github.dansmithy.spring;

import javax.ws.rs.core.Response;

import com.github.dansmithy.sanjuan.config.ConfigurationStore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.SkeletonBddTestRunner;
import com.github.dansmithy.driver.GameDriver;
import com.github.dansmithy.sanjuan.exception.SanJuanRuntimeException;
import com.github.dansmithy.sanjuan.exception.mapper.SanJuanRuntimeExceptionMapper;
import com.github.dansmithy.sanjuan.rest.jaxrs.CardResource;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameResource;
import com.github.dansmithy.sanjuan.rest.jaxrs.UserResource;
import com.github.dansmithy.sanjuan.twitter.service.TwitterUserStore;

public class BddSpringTestRunner extends SkeletonBddTestRunner<GameDriver> {

	private static final SanJuanRuntimeExceptionMapper EXCEPTION_MAPPER = new SanJuanRuntimeExceptionMapper();
	
	private final GameResource gameResource;
	private final UserResource userResource;
	private final CardResource cardResource;
	private TwitterUserStore sessionProvider;
	private final String adminUsername;

	public BddSpringTestRunner() {
		ApplicationContext context = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/applicationContext.xml");
		gameResource = context.getBean(GameResource.class);
		userResource = context.getBean(UserResource.class);
		cardResource = context.getBean(CardResource.class);
		sessionProvider = context.getBean(TwitterUserStore.class);
        adminUsername = context.getBean(ConfigurationStore.class).getAdminUsername();
	}

	@Override
	public GameDriver createContext() {
		return new GameSpringDriver(gameResource, userResource, cardResource, sessionProvider, adminUsername);
	}

	public void afterTest(GameDriver context) {
		context.cleanup();
	}

	@Override
	protected void doEvent(BddPart<GameDriver> given, GameDriver context) {
		try {
			super.doGiven(given, context);
		} catch (SanJuanRuntimeException e) {
			Response response = EXCEPTION_MAPPER.toResponse(e);
			context.setLastResponse(new SpringResponse(response.getEntity(), response.getStatus())); 
		}
	}

	@Override
	protected void doOutcome(BddPart<GameDriver> outcome, GameDriver context) {
		try {
			super.doOutcome(outcome, context);
		} catch (SanJuanRuntimeException e) {
			Response response = EXCEPTION_MAPPER.toResponse(e);
			context.setLastResponse(new SpringResponse(response.getEntity(), response.getStatus())); 
		}
	}

	@Override
	public void shutdown() {
		// do nothing
	}
	
	
	
	

}
