package com.github.dansmithy.sanjuan.exception;

public class ResourceNotFoundException extends SanJuanException {

	public ResourceNotFoundException(String message) {
		super(message);
	}

	@Override
	public String getType() {
		return "NOT_FOUND";
	}

}
