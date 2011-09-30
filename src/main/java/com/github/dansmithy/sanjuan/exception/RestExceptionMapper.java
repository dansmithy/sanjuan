package com.github.dansmithy.sanjuan.exception;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
@Named
public class RestExceptionMapper implements ExceptionMapper<SanJuanException> {

	/**
	 */
	@Override
	public Response toResponse(SanJuanException e) {

		return Response
			.status(convertToStatus(e))
			.type(MediaType.APPLICATION_JSON)
			.entity(createError(e))
			.build();
	}

	private JsonError createError(SanJuanException e) {
		return new JsonError(e.getMessage(), e.getType());
	}

	private Status convertToStatus(SanJuanException e) {
		if (e instanceof AccessException) {
			return Status.UNAUTHORIZED;
		} else if (e instanceof ResourceNotFoundException) {
			return Status.NOT_FOUND;
		} else {
			return Status.INTERNAL_SERVER_ERROR;
		}
	}

}
