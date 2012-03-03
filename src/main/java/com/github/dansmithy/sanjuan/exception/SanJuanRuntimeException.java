package com.github.dansmithy.sanjuan.exception;

/**
 * Superclass of all SanJuan application exceptions
 */
public abstract class SanJuanRuntimeException extends RuntimeException {

	public SanJuanRuntimeException(String message) {
		super(message);
	}

    public SanJuanRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String getType();
	
}
