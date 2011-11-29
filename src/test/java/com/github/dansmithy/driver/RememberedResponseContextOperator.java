package com.github.dansmithy.driver;

import com.github.restdriver.serverdriver.http.response.Response;

public class RememberedResponseContextOperator implements ContextOperator<Response, GameDriver> {

	private String rememberedKey;

	public RememberedResponseContextOperator(String rememberedKey) {
		super();
		this.rememberedKey = rememberedKey;
	}

	@Override
	public Response getFromContext(GameDriver context) {
		return context.getRememberedResponse(rememberedKey);
	}
}
