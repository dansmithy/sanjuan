package com.github.dansmithy.sanjuan.rest.jaxrs;

import org.junit.Test;

public class GameViewsTest {

	@Test
	public void ensureCoverage() {
		new GameViews();
		new GameViews.Full();
		new GameViews.PlayDetail();
	}
}
