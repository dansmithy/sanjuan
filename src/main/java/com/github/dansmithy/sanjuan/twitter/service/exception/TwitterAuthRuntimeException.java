package com.github.dansmithy.sanjuan.twitter.service.exception;

public class TwitterAuthRuntimeException extends RuntimeException {

	public TwitterAuthRuntimeException() {
		super();
	}

	public TwitterAuthRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public TwitterAuthRuntimeException(String message) {
		super(message);
	}

	public TwitterAuthRuntimeException(Throwable cause) {
		super(cause);
	}
}
