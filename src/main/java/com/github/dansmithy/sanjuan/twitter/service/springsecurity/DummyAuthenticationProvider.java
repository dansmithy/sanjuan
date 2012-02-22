package com.github.dansmithy.sanjuan.twitter.service.springsecurity;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class DummyAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		throw new UnsupportedOperationException("This is a DummyAuthenticationProvider and should never be called");
	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return false;
	}

}
