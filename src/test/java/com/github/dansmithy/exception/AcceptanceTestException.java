package com.github.dansmithy.exception;

public class AcceptanceTestException extends RuntimeException {

	public AcceptanceTestException() {
		super();
	}

	public AcceptanceTestException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcceptanceTestException(String message) {
		super(message);
	}

	public AcceptanceTestException(Throwable cause) {
		super(cause);
	}
}
