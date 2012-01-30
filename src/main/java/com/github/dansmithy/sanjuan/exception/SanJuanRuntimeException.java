package com.github.dansmithy.sanjuan.exception;

public abstract class SanJuanRuntimeException extends RuntimeException {

	public SanJuanRuntimeException(String message) {
		super(message);
	}

	public abstract String getType();
	
}
