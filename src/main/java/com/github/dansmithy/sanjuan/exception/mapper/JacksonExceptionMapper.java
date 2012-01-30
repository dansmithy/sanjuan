package com.github.dansmithy.sanjuan.exception.mapper;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;

import com.github.dansmithy.sanjuan.exception.model.JsonError;


@Provider
@Named
public class JacksonExceptionMapper implements ExceptionMapper<JsonProcessingException> {

	/**
	 */
	@Override
	public Response toResponse(JsonProcessingException e) {

		return Response
			.status(convertToStatus(e))
			.type(MediaType.APPLICATION_JSON)
			.entity(createError(e))
			.build();
	}

	private JsonError createError(JsonProcessingException e) {
		if (e instanceof JsonMappingException) {
			return new JsonError(e.getMessage(), "CANNOT_HANDLE_REQUEST");
		} else {
			return new JsonError(e.getMessage(), "JSON_PROCESSING");
		}
	}

	private int convertToStatus(JsonProcessingException e) {
		if (e instanceof JsonMappingException) {
			return Status.BAD_REQUEST.getStatusCode();
		} else {
			return Status.BAD_REQUEST.getStatusCode();
		}
	}

}
