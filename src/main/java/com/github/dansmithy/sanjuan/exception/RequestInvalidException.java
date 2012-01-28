package com.github.dansmithy.sanjuan.exception;

public class RequestInvalidException extends SanJuanException {

	private static final String DEFAULT_TYPE = "INVALID_REQUEST";
	
	private String type = DEFAULT_TYPE;
	
	public RequestInvalidException(String message) {
		this(message, DEFAULT_TYPE);
	}
	
	public RequestInvalidException(String message, String type) {
		super(message);
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}

}
