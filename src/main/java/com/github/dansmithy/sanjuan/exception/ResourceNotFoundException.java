package com.github.dansmithy.sanjuan.exception;

public class ResourceNotFoundException extends SanJuanException {

	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}
	
	@Override
	public String getType() {
		return "NOT_FOUND";
	}

}
