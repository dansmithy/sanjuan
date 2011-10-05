package com.github.dansmithy.sanjuan.exception;


public class IllegalGameStateException extends SanJuanException {

	public IllegalGameStateException() {
		super();
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
		return "ILLEGALSTATE";
	}
}
