package com.github.dansmithy.sanjuan.exception;

public class AuthenticatedUserDoesNotMatchSubmittedData extends AccessException {

	public AuthenticatedUserDoesNotMatchSubmittedData() {
		super();
	}

	public AuthenticatedUserDoesNotMatchSubmittedData(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticatedUserDoesNotMatchSubmittedData(String message) {
		super(message);
	}

	public AuthenticatedUserDoesNotMatchSubmittedData(Throwable cause) {
		super(cause);
	}
	
	@Override
	public String getType() {
		return "NOT_CORRECT_USER";
	}

}
