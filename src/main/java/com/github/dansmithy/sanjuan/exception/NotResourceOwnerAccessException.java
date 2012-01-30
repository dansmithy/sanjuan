package com.github.dansmithy.sanjuan.exception;

public class NotResourceOwnerAccessException extends AccessException {

	public static final String NOT_CORRECT_USER = "NOT_CORRECT_USER";
	public static final String NOT_YOUR_GAME = "NOT_YOUR_GAME";
	
	private static final String DEFAULT_TYPE = "ACCESS_DENIED";

	private String type = DEFAULT_TYPE;
	
	public NotResourceOwnerAccessException(String message, String type) {
		super(message);
		this.type = type;
	}
	
	@Override
	public String getType() {
		return type;
	}
}
