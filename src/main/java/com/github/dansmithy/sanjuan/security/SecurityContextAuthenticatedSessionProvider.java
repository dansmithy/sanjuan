package com.github.dansmithy.sanjuan.security;

import javax.inject.Named;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.dansmithy.sanjuan.model.User;
import com.github.dansmithy.sanjuan.security.user.SanJuanUserDetails;

/**
 * This implementation retrieves the authenticated user from the session using the Spring SecurityContext static method.
 */
@Named
public class SecurityContextAuthenticatedSessionProvider implements AuthenticatedSessionProvider {

	@Override
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public UserDetails getUserDetails() {
		Authentication authentication = getAuthentication();
		Object principal = authentication.getPrincipal();

		if (principal instanceof UserDetails) {
			return (UserDetails) principal;
		} else {
			return SanJuanUserDetails.DISABLED_USER;
		}
	}

	@Override
	public String getAuthenticatedUsername() {
		UserDetails userDetails = getUserDetails();
		if (userDetails.isEnabled()) {
			return userDetails.getUsername();
		} else {
			throw new RuntimeException("Attempt to obtain username when there is no authenticated user.");
		}
	}
	
	public void addUser(User user) {
		SanJuanUserDetails userDetails = new SanJuanUserDetails(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

}