package com.github.dansmithy.sanjuan.exception;

/**
 * Results in an HTTP_NOT_FOUND (404) response code.
 */
public class ResourceNotFoundRuntimeException extends SanJuanRuntimeException {

	public ResourceNotFoundRuntimeException(String message) {
		super(message);
	}

	@Override
	public String getType() {
		return "NOT_FOUND";
	}

}
