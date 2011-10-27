package com.github.dansmithy.sanjuan.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.dansmithy.sanjuan.model.User;


/**
 * An interface to access the authenticated user stored in the Session.
 */
public interface AuthenticatedSessionProvider {

	Authentication getAuthentication();

	UserDetails getUserDetails();

	String getAuthenticatedUsername();

}
