package com.github.dansmithy.sanjuan.exception;

public class NotResourceOwnerAccessException extends AccessException {

	public NotResourceOwnerAccessException(String message) {
		super(message);
	}

	@Override
	public String getType() {
		return "NOT_CORRECT_USER";
	}

}
