package com.github.dansmithy.sanjuan.exception;

/**
 * Results in an HTTP_CONFLICT (409) response code.
 * 
 * Caused by an attempt to change a value that cannot be changed.
 */
public class ImmutableDataRuntimeException extends SanJuanRuntimeException {

	public ImmutableDataRuntimeException(String message) {
		super(message);
	}

	@Override
	public String getType() {
		return "IMMUTABLE";
	}

}
