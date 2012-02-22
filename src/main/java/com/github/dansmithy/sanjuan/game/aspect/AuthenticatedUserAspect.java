package com.github.dansmithy.sanjuan.game.aspect;

import javax.inject.Inject;
import javax.inject.Named;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import com.github.dansmithy.sanjuan.twitter.service.TwitterUserStore;

@Named
@Aspect
public class AuthenticatedUserAspect {

	private TwitterUserStore authenticatedSessionProvider;
	
	@Inject
	public AuthenticatedUserAspect(
			TwitterUserStore authenticatedSessionProvider) {
		super();
		this.authenticatedSessionProvider = authenticatedSessionProvider;
	}

	@Pointcut("within(com.github.dansmithy.sanjuan.rest.beans..*)")
	public void restBeanMethods() {}

	@Before("com.github.dansmithy.sanjuan.game.aspect.AuthenticatedUserAspect.restBeanMethods()")
	public void setAuthenticatedUser() {
		AuthenticatedUser.set(authenticatedSessionProvider.getCurrentUser().getName());
	}

}
