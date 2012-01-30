package com.github.dansmithy.sanjuan.exception;

public class ResourceNotFoundRuntimeException extends SanJuanRuntimeException {

	public ResourceNotFoundRuntimeException(String message) {
		super(message);
	}

	@Override
	public String getType() {
		return "NOT_FOUND";
	}

}
