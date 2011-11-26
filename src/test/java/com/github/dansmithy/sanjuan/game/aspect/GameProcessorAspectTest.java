package com.github.dansmithy.sanjuan.game.aspect;

import org.junit.Test;

public class GameProcessorAspectTest {

	@Test
	public void ensureCoverage() {
		new GameProcessorAspect(null).methodAnnotatedWithProcessGame();
	}
}
