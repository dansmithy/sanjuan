package com.github.dansmithy.sanjuan.security.spring;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * This is an implementation of the Spring Security {@link AuthenticationFailureHandler} interface. This interface is used when an attempt to
 * authenticate fails, eg. due to bad credentials.
 *
 * This implementation returns an HTTP 403 response with a JSON body containing a simple message.
 *
 * This is required by the UsernamePasswordAuthenticationFilter (or our extended version).
 *
 * TODO Make the response body better, and in line with Nokia error messages.
 */
public class Http403AuthenticationFailureHandler implements AuthenticationFailureHandler {

	static final String ACCESS_DENIED_JSON = "{ \"message\" : \"Access denied\" }";

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON);
		response.getWriter().write(ACCESS_DENIED_JSON);
	}

}
