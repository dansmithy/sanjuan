package com.github.dansmithy.sanjuan.exception;


public class IllegalGameStateException extends SanJuanException {

	public static final String NOT_AWAITING_ROLE_CHOICE = "NOT_AWAITING_ROLE_CHOICE";
	public static final String PHASE_NOT_ACTIVE = "PHASE_NOT_ACTIVE";
	public static final String PLAY_NOT_ACTIVE = "PLAY_NOT_ACTIVE";
	
	private static final String DEFAULT_TYPE = "ILLEGALSTATE";

	
	private String type = DEFAULT_TYPE;

	public IllegalGameStateException() {
		super();
	}

	public IllegalGameStateException(String message, String type) {
		super(message);
		this.type = type;
	}
	
	public IllegalGameStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalGameStateException(String message) {
		super(message);
	}

	public IllegalGameStateException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getType() {
		return type;
	}
}
