package com.github.dansmithy.sanjuan.exception;

public class NotResourceOwnerAccessException extends AccessException {

	public NotResourceOwnerAccessException() {
		super();
	}

	public NotResourceOwnerAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotResourceOwnerAccessException(String message) {
		super(message);
	}

	public NotResourceOwnerAccessException(Throwable cause) {
		super(cause);
	}
	
	@Override
	public String getType() {
		return "NOT_OWNER";
	}

}
