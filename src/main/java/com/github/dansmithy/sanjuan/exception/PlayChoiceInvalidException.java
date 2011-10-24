package com.github.dansmithy.sanjuan.exception;

public class PlayChoiceInvalidException extends SanJuanException {

	public PlayChoiceInvalidException() {
		super();
	}

	public PlayChoiceInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlayChoiceInvalidException(String message) {
		super(message);
	}

	public PlayChoiceInvalidException(Throwable cause) {
		super(cause);
	}
	
	@Override
	public String getType() {
		return "INVALID_CHOICE";
	}

}
