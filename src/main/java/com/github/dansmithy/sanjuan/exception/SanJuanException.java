package com.github.dansmithy.sanjuan.exception;

public abstract class SanJuanException extends RuntimeException {

	public SanJuanException(String message) {
		super(message);
	}

	public abstract String getType();
	
}
