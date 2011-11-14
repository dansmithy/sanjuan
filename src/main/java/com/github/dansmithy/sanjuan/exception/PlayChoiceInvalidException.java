package com.github.dansmithy.sanjuan.exception;

public class PlayChoiceInvalidException extends SanJuanException {

	public static final String UNDERPAID = "UNDERPAID";
	public static final String OVERPAID = "OVERPAID";
	public static final String NOT_OWNED_PAYMENT = "NOT_OWNED_PAYMENT";
	public static final String NOT_OWNED_BUILD_CHOICE = "NOT_OWNED_BUILD_CHOICE";
	public static final String NOT_OWNED_COUNCIL_DISCARD = "NOT_OWNED_COUNCIL_DISCARD";
	public static final String NOT_OWNED_FACTORY = "NOT_OWNED_FACTORY";
	public static final String UNDER_DISCARD = "UNDER_DISCARD";
	public static final String OVER_DISCARD = "OVER_DISCARD";
	public static final String OVER_PRODUCE = "OVER_PRODUCE";
	public static final String OVER_TRADE = "OVER_TRADE";
	public static final String NOT_EMPTY_FACTORY = "NOT_EMPTY_FACTORY";
	public static final String NOT_FULL_FACTORY = "NOT_FULL_FACTORY";
	public static final String DUPLICATE_CHOICE = "DUPLICATE_CHOICE";
	
	private static final String DEFAULT_TYPE = "INVALID_CHOICE";
	
	private String type = DEFAULT_TYPE;
	
	public PlayChoiceInvalidException() {
		super();
	}

	public PlayChoiceInvalidException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PlayChoiceInvalidException(String message, String type) {
		super(message);
		this.type = type;
	}

	public PlayChoiceInvalidException(String message) {
		super(message);
	}

	public PlayChoiceInvalidException(Throwable cause) {
		super(cause);
	}
	
	@Override
	public String getType() {
		return type;
	}

}
