package com.github.dansmithy.sanjuan.rest.exception;

import com.github.dansmithy.sanjuan.exception.SanJuanException;

public class AccessException extends SanJuanException {

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
