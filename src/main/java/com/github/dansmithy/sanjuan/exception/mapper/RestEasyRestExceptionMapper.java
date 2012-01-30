package com.github.dansmithy.sanjuan.exception.mapper;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.UnauthorizedException;

import com.github.dansmithy.sanjuan.exception.model.JsonError;


@Provider
@Named
public class RestEasyRestExceptionMapper implements ExceptionMapper<LoggableFailure> {

	/**
	 */
	@Override
	public Response toResponse(LoggableFailure e) {

		return Response
			.status(convertToStatus(e))
			.type(MediaType.APPLICATION_JSON)
			.entity(createError(e))
			.build();
	}

	private JsonError createError(LoggableFailure e) {
		if (e instanceof UnauthorizedException) {
			return new JsonError(e.getMessage(), "NO_AUTH");
		} else {
			return new JsonError(e.getMessage(), "RESTEASY");
		}
	}

	private int convertToStatus(LoggableFailure e) {
		return e.getErrorCode();
	}

}
