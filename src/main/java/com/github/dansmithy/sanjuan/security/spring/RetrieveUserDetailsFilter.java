package com.github.dansmithy.sanjuan.security.spring;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.web.filter.GenericFilterBean;

import com.github.dansmithy.sanjuan.security.DefaultJsonUserDetailsProducer;
import com.github.dansmithy.sanjuan.security.JsonUserDetailsProducer;

/**
 * Returns the currently authenticated user, if there is one.
 *
 * This is added to the Spring Security filter chain, but only works on requests to a particular URL (in this case "/loginDetails").
 */
public class RetrieveUserDetailsFilter extends GenericFilterBean {

	public static final String LOGIN_DETAILS_URL = "/loginDetails";

	private JsonUserDetailsProducer jsonUserDetailsProducer = new DefaultJsonUserDetailsProducer();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (shouldRetrieveLoginDetails((HttpServletRequest) request, (HttpServletResponse) response)) {
			response.setContentType(MediaType.APPLICATION_JSON);
			response.getWriter().write(jsonUserDetailsProducer.generateUserDetailsJson());
		} else {
			chain.doFilter(request, response);
		}
	}

	/**
	 * Verifies whether the URL matches the LOGIN_DETAILS_URL url.
	 * @param request
	 * @param response
	 * @return
	 */
	protected boolean shouldRetrieveLoginDetails(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();
		int pathParamIndex = uri.indexOf(';');

		if (pathParamIndex > 0) {
			// strip everything after the first semi-colon
			uri = uri.substring(0, pathParamIndex);
		}

		if ("".equals(request.getContextPath())) {
			return uri.endsWith(LOGIN_DETAILS_URL);
		}

		return uri.endsWith(request.getContextPath() + LOGIN_DETAILS_URL);
	}

}
