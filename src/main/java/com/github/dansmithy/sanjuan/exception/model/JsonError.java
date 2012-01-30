package com.github.dansmithy.sanjuan.exception.model;

public class JsonError {

	private String message;
	private String code;
	
	public JsonError(String message, String code) {
		super();
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}

	
}
