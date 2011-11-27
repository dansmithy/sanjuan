package com.github.dansmithy.sanjuan.exception;


public class ImmutableDataException extends SanJuanException {

	public ImmutableDataException(String message) {
		super(message);
	}

	@Override
	public String getType() {
		return "IMMUTABLE";
	}

}
