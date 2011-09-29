package com.github.dansmithy.sanjuan.security.spring;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.github.dansmithy.sanjuan.security.user.SimpleCredentials;

/**
 * This class is part of the Spring Security filter chain.
 *
 * If the request is a POST to the login URL, it obtains the username and password from the request, and uses these details to attempt authentication.
 *
 * The class it extends expects the username and password to be part an HTML Form POST. This customised version instead expects the username and
 * password to be provided as a JSON object in the body of the request. See {@link SimpleCredentials} for more details.
 */
public class JsonPayloadUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final String KEY_FOR_TEMPORARY_CREDENTIALS_STORAGE = "_credentials";

	@Override
	protected String obtainPassword(HttpServletRequest request) {
		return obtainCredentials(request).getPassword();
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		return obtainCredentials(request).getUsername();
	}

	/**
	 * The Spring parent class exposes separate 'obtainUsername' and 'obtainPassword' methods, but we need to extract the JSON body containing both in
	 * one go.
	 *
	 * Therefore the first time this is called it will extract the JSON body as a {@link SimpleCredentials} object, and temporarily store this in the
	 * request object. The second time this method is called it only has to return the {@link SimpleCredentials} stored in the request.
	 *
	 * @param request
	 * @return
	 */
	private SimpleCredentials obtainCredentials(HttpServletRequest request) {

		SimpleCredentials storedCredentials = (SimpleCredentials) request.getAttribute(KEY_FOR_TEMPORARY_CREDENTIALS_STORAGE);
		if (storedCredentials == null) {
			storedCredentials = extractCredentialsFromRequest(request);
			request.setAttribute(KEY_FOR_TEMPORARY_CREDENTIALS_STORAGE, storedCredentials);
		}
		return storedCredentials;

	}

	/**
	 * If there is a problem extracting the credentials from the request, we just return an empty object. The Spring parent class converts null
	 * username/passwords to empty string anyhow, so it doesn't matter which of these we return.
	 *
	 * @param request
	 * @return
	 */
	private SimpleCredentials extractCredentialsFromRequest(
						HttpServletRequest request) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(request.getInputStream(), SimpleCredentials.class);
		} catch (JsonParseException e) {
			return SimpleCredentials.EMPTY_CREDENTIALS;
		} catch (JsonMappingException e) {
			return SimpleCredentials.EMPTY_CREDENTIALS;
		} catch (IOException e) {
			return SimpleCredentials.EMPTY_CREDENTIALS;
		}
	}

}
