package com.github.dansmithy.sanjuan.exception;

/**
 * Results in an HTTP_BAD_REQUEST (400) response code.
 */
public class PlayChoiceInvalidRuntimeException extends SanJuanRuntimeException {

	public static final String UNDERPAID = "UNDERPAID";
	public static final String OVERPAID = "OVERPAID";
	public static final String NOT_OWNED_PAYMENT = "NOT_OWNED_PAYMENT";
	public static final String NOT_OWNED_BUILD_CHOICE = "NOT_OWNED_BUILD_CHOICE";
	public static final String NOT_OWNED_COUNCIL_DISCARD = "NOT_OWNED_COUNCIL_DISCARD";
	public static final String NOT_OWNED_HAND_CARD = "NOT_OWNED_HAND_CARD";
	public static final String NOT_OWNED_FACTORY = "NOT_OWNED_FACTORY";
	public static final String UNDER_DISCARD = "UNDER_DISCARD";
	public static final String OVER_DISCARD = "OVER_DISCARD";
	public static final String OVER_PRODUCE = "OVER_PRODUCE";
	public static final String OVER_TRADE = "OVER_TRADE";
	public static final String NOT_EMPTY_FACTORY = "NOT_EMPTY_FACTORY";
	public static final String NOT_FULL_FACTORY = "NOT_FULL_FACTORY";
	public static final String DUPLICATE_CHOICE = "DUPLICATE_CHOICE";
	public static final String INVALID_ROLE = "INVALID_ROLE";
	
	private String type;
	
	public PlayChoiceInvalidRuntimeException(String message, String type) {
		super(message);
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}

}
