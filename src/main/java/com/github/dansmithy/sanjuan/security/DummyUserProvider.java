package com.github.dansmithy.sanjuan.security;

import javax.inject.Named;

@Named
public class DummyUserProvider implements UserProvider {

	@Override
	public String getLoggedInUser() {
		return "daniel";
	}

}
