package com.github.dansmithy.sanjuan.exception;

/**
 * Results in an HTTP_BAD_REQUEST (400) response code.
 */
public class RequestInvalidRuntimeException extends SanJuanRuntimeException {

	private static final String DEFAULT_TYPE = "INVALID_REQUEST";
	
	private String type = DEFAULT_TYPE;
	
	public RequestInvalidRuntimeException(String message) {
		this(message, DEFAULT_TYPE);
	}
	
	public RequestInvalidRuntimeException(String message, String type) {
		super(message);
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}

}
