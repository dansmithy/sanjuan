package com.github.dansmithy.sanjuan.twitter.service.springsecurity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.github.dansmithy.sanjuan.exception.model.JsonError;

public class JsonResponseHttp403AuthenticationEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {

	private static final String ERROR_TYPE = "NO_AUTH";
	
	public JsonResponseHttp403AuthenticationEntryPoint() {
		super();
	}

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		
		sendResponse(response, new JsonError("You need to be logged in to access this resource.", ERROR_TYPE), HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	@Override
	public void handle(HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException,
			ServletException {
		
		sendResponse(response, new JsonError("You do not have permissions to access this resource.", "NO_PERM"), HttpServletResponse.SC_FORBIDDEN);
	}		

	private void sendResponse(HttpServletResponse response,
			JsonError jsonError, int code) throws IOException {
		response.setStatus(code);
		response.setContentType(MediaType.APPLICATION_JSON);
		response.getWriter().write(jsonError.toJsonString());
	}
	

}
