package com.github.dansmithy.driver;

public interface ResponseMatcher {

	String getPurpose();

	boolean matches(GameDriver context);

}
