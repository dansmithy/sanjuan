package com.github.dansmithy.sanjuan.exception;

public class SanJuanException extends RuntimeException {

	public SanJuanException() {
		super();
	}

	public SanJuanException(String message, Throwable cause) {
		super(message, cause);
	}

	public SanJuanException(String message) {
		super(message);
	}

	public SanJuanException(Throwable cause) {
		super(cause);
	}
}
