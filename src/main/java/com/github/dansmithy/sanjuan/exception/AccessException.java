package com.github.dansmithy.sanjuan.exception;


public abstract class AccessException extends SanJuanException {

	public AccessException() {
		super();
	}

	public AccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessException(String message) {
		super(message);
	}

	public AccessException(Throwable cause) {
		super(cause);
	}
}
