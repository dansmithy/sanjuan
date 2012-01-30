package com.github.dansmithy.sanjuan.security.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.github.dansmithy.sanjuan.exception.model.JsonError;
import com.github.dansmithy.sanjuan.security.AuthenticatedSessionProvider;

public class JsonResponseHttp403AuthenticationEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {

	private AuthenticatedSessionProvider authenticatedSessionProvider;
	private ObjectMapper mapper = new ObjectMapper();
	
	private static final String DEFAULT_JSON = "{ 'message' : 'Unable to access resource', 'type', 'NO_AUTH' }";
	
	@Autowired
	public JsonResponseHttp403AuthenticationEntryPoint(
			AuthenticatedSessionProvider authenticatedSessionProvider) {
		super();
		this.authenticatedSessionProvider = authenticatedSessionProvider;
	}

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		
		sendResponse(response, new JsonError("You need to be logged in to access this resource.", "NO_AUTH"), HttpServletResponse.SC_UNAUTHORIZED);
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
		response.getWriter().write(convertToJson(jsonError));
	}
	
	private String convertToJson(JsonError jsonError) {
		try {
			return mapper.writeValueAsString(jsonError);
		} catch (JsonGenerationException e) {
			return DEFAULT_JSON;
		} catch (JsonMappingException e) {
			return DEFAULT_JSON;
		} catch (IOException e) {
			return DEFAULT_JSON;
		}

	}

}
