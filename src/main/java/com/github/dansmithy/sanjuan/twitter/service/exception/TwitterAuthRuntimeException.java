package com.github.dansmithy.sanjuan.twitter.service.exception;

import com.github.dansmithy.sanjuan.exception.SanJuanRuntimeException;

public class TwitterAuthRuntimeException extends SanJuanRuntimeException {

    public TwitterAuthRuntimeException(String message) {
        super(message);
    }

	public TwitterAuthRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

    @Override
    public String getType() {
        return "TWITTER_AUTH";
    }

}
