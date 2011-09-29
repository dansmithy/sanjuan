package com.github.dansmithy.sanjuan.security;

import java.io.IOException;
import java.util.Collection;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.dansmithy.sanjuan.security.user.JsonUserDetails;

/**
 * Generates a JSON String representing the currently authenticated user. If no user is authenticated, it should return an empty JSON object.
 *
 * This implementation uses a {@link AuthenticatedSessionProvider} that provides access to the Spring Security object {@link UserDetails} stored in
 * the Session. It then converts this to a from that can easily be serialized to JSON.
 */
public class DefaultJsonUserDetailsProducer implements JsonUserDetailsProducer {

	private AuthenticatedSessionProvider authenticatedSessionProvider;
	private ObjectMapper mapper;
	private static final String EMPTY_JSON = "{}";

	public DefaultJsonUserDetailsProducer() {
		this(new SecurityContextAuthenticatedSessionProvider(), new ObjectMapper());
	}

	public DefaultJsonUserDetailsProducer(AuthenticatedSessionProvider authenticatedSessionProvider, ObjectMapper mapper) {
		this.authenticatedSessionProvider = authenticatedSessionProvider;
		this.mapper = mapper;
	}

	@Override
	public String generateUserDetailsJson() {
		Authentication authentication = authenticatedSessionProvider.getAuthentication();
		if (isAuthenticated(authentication)) {
			return createResponse(authentication);
		}
		return EMPTY_JSON;
	}

	private boolean isAuthenticated(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated();
	}

	private String createResponse(Authentication authentication) {
		JsonUserDetails userDetails = new JsonUserDetails();

		Object principal = authentication.getPrincipal();

		addAuthorities(userDetails, authentication.getAuthorities());
		if (principal instanceof UserDetails) {

			UserDetails logingUserDetails = (UserDetails) principal;
			userDetails.setUsername(logingUserDetails.getUsername());
			return convertToJson(userDetails);

		} else {

			// should never happen!
			return EMPTY_JSON;
		}

	}

	private String convertToJson(JsonUserDetails userDetails) {
		try {
			return mapper.writeValueAsString(userDetails);
		} catch (JsonGenerationException e) {
			return EMPTY_JSON;
		} catch (JsonMappingException e) {
			return EMPTY_JSON;
		} catch (IOException e) {
			return EMPTY_JSON;
		}

	}

	private void addAuthorities(JsonUserDetails userDetails, Collection<GrantedAuthority> authorities) {
		String[] simpleAuthorities = new String[authorities.size()];
		int index = 0;
		for (GrantedAuthority authority : authorities) {
			simpleAuthorities[index] = authority.getAuthority();
			index++;
		}
		userDetails.setAuthorities(simpleAuthorities);
	}
}