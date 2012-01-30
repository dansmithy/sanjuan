package com.github.dansmithy.sanjuan.exception;

/**
 * Results in an HTTP_CONFLICT (409) response code.
 *
 */
public class IllegalGameStateException extends SanJuanException {

	public static final String NOT_AWAITING_ROLE_CHOICE = "NOT_AWAITING_ROLE_CHOICE";
	public static final String OWNER_CANNOT_QUIT = "OWNER_CANNOT_QUIT";
	public static final String PHASE_NOT_ACTIVE = "PHASE_NOT_ACTIVE";
	public static final String PLAY_NOT_ACTIVE = "PLAY_NOT_ACTIVE";
	public static final String NOT_RECRUITING = "NOT_RECRUITING";
	public static final String NOT_ACTIVE_STATE = "NOT_ACTIVE_STATE";
	public static final String NOT_PLAYING = "NOT_PLAYING";
	public static final String NOT_ENOUGH_PLAYERS = "NOT_ENOUGH_PLAYERS";
	public static final String TOO_MANY_PLAYERS = "TOO_MANY_PLAYERS";
	public static final String ALREADY_PLAYER = "ALREADY_PLAYER";
	public static final String ROLE_ALREADY_TAKEN = "ROLE_ALREADY_TAKEN";
	public static final String BUILDING_ALREADY_BUILT = "BUILDING_ALREADY_BUILT";
	
	private static final String DEFAULT_TYPE = "ILLEGALSTATE";

	private String type = DEFAULT_TYPE;

	public IllegalGameStateException(String message, String type) {
		super(message);
		this.type = type;
	}
	
	@Override
	public String getType() {
		return type;
	}
}
