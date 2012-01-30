package com.github.dansmithy.sanjuan.exception;

/**
 * Results in an HTTP_UNAUTHORIZED (401) response code.
 */
public class AccessUnauthorizedRuntimeException extends SanJuanRuntimeException {

	public static final String NOT_CORRECT_USER = "NOT_CORRECT_USER";
	public static final String NOT_YOUR_GAME = "NOT_YOUR_GAME";
	public static final String NOT_MATCHING_PLAYER = "NOT_MATCHING_PLAYER";
	
	private String type;
	
	public AccessUnauthorizedRuntimeException(String message, String type) {
		super(message);
		this.type = type;
	}
	
	@Override
	public String getType() {
		return type;
	}
}
