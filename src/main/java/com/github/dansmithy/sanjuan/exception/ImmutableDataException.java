package com.github.dansmithy.sanjuan.exception;


public class ImmutableDataException extends SanJuanException {

	public ImmutableDataException() {
		super();
	}

	public ImmutableDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImmutableDataException(String message) {
		super(message);
	}

	public ImmutableDataException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getType() {
		return "IMMUTABLE";
	}

}
