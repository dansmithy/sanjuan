package com.github.dansmithy.sanjuan.exception;

public class AuthenticatedUserDoesNotMatchSubmittedData extends AccessException {

	public AuthenticatedUserDoesNotMatchSubmittedData(String message) {
		super(message);
	}

	@Override
	public String getType() {
		return "NOT_CORRECT_USER";
	}

}
