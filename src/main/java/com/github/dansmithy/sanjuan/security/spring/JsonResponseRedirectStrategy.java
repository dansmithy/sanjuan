package com.github.dansmithy.sanjuan.security.spring;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.security.web.RedirectStrategy;

import com.github.dansmithy.sanjuan.security.DefaultJsonUserDetailsProducer;
import com.github.dansmithy.sanjuan.security.JsonUserDetailsProducer;

/**
 * This class generates the JSON response on a successful authentication.
 *
 * This is a bit of a corruption of the purpose of "RedirectStrategy", but it is an easy way to put this functionality into Spring Security.
 *
 * Normally the purpose of this would be to redirect the user to a URL they were trying to get to before being forced to authenticate. This URL would
 * have been saved in the session and then this class could perform the client-side redirect to that 'targetUrl'.
 *
 * However, in this instance we do not perform any redirect, but instead return a 200 response with a body containing a JSON-representation of the
 * authenticated user.
 *
 * This class is required by the SuccessHandler we use, which in turn is required by the UsernamePasswordAuthenticationFilter (or our extended version).
 */
public class JsonResponseRedirectStrategy implements RedirectStrategy {

	private JsonUserDetailsProducer jsonUserDetailsProducer;

	public JsonResponseRedirectStrategy() {
		this(new DefaultJsonUserDetailsProducer());
	}

	public JsonResponseRedirectStrategy(JsonUserDetailsProducer jsonUserDetailsProducer) {
		this.jsonUserDetailsProducer = jsonUserDetailsProducer;
	}

	@Override
	public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON);
		response.getWriter().write(jsonUserDetailsProducer.generateUserDetailsJson());
	}

}