package com.github.dansmithy.sanjuan.exception;


public class SanJuanUnexpectedException extends SanJuanException {

	public SanJuanUnexpectedException() {
		super();
	}

	public SanJuanUnexpectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public SanJuanUnexpectedException(String message) {
		super(message);
	}

	public SanJuanUnexpectedException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getType() {
		return "UNEXPECTED";
	}

}
