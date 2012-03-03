package com.github.dansmithy.sanjuan.exception.mapper;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.github.dansmithy.sanjuan.exception.AccessUnauthorizedRuntimeException;
import com.github.dansmithy.sanjuan.exception.IllegalGameStateRuntimeException;
import com.github.dansmithy.sanjuan.exception.PlayChoiceInvalidRuntimeException;
import com.github.dansmithy.sanjuan.exception.RequestInvalidRuntimeException;
import com.github.dansmithy.sanjuan.exception.ResourceNotFoundRuntimeException;
import com.github.dansmithy.sanjuan.exception.SanJuanRuntimeException;
import com.github.dansmithy.sanjuan.exception.model.JsonError;
import com.github.dansmithy.sanjuan.twitter.service.exception.TwitterAuthRuntimeException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;


@Provider
@Named
public class SanJuanRuntimeExceptionMapper implements ExceptionMapper<SanJuanRuntimeException> {

    private static final String LOGIN_ERROR = "/login_error.html";

    /**
	 */
	@Override
	public Response toResponse(SanJuanRuntimeException e) {

        if (e instanceof TwitterAuthRuntimeException) {
            return createRedirectToUrl(LOGIN_ERROR);
        } else {
            return Response
                .status(convertToStatus(e))
                .type(MediaType.APPLICATION_JSON)
                .entity(createError(e))
                .build();
        }
	}

	private JsonError createError(SanJuanRuntimeException e) {
		return new JsonError(e.getMessage(), e.getType());
	}

	private Status convertToStatus(SanJuanRuntimeException e) {
		if (e instanceof AccessUnauthorizedRuntimeException) {
			return Status.UNAUTHORIZED;
        } else if (e instanceof ResourceNotFoundRuntimeException) {
            return Status.NOT_FOUND;
		} else if (e instanceof IllegalGameStateRuntimeException) {
			return Status.CONFLICT;
		} else if (e instanceof PlayChoiceInvalidRuntimeException) {
			return Status.BAD_REQUEST;
		} else if (e instanceof RequestInvalidRuntimeException) {
			return Status.BAD_REQUEST;
		} else {
			return Status.INTERNAL_SERVER_ERROR;
		}
	}

    private Response createRedirectToUrl(String url) {
        return Response.status(HttpURLConnection.HTTP_MOVED_TEMP).location(createUri(url)).build();
    }

    private URI createUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
