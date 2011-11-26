package com.github.dansmithy.sanjuan.model;

import static org.hamcrest.Matchers.*;

import org.junit.Assert;
import org.junit.Test;

public class EnumTests {

	@Test
	public void ensureCoverageEnumTests() {
	
		Assert.assertThat(GameState.valueOf(GameState.COMPLETED.toString()), is(equalTo(GameState.COMPLETED)));
		Assert.assertThat(RoundState.valueOf(RoundState.COMPLETED.toString()), is(equalTo(RoundState.COMPLETED)));
		Assert.assertThat(PhaseState.valueOf(PhaseState.COMPLETED.toString()), is(equalTo(PhaseState.COMPLETED)));
		Assert.assertThat(PlayState.valueOf(PlayState.COMPLETED.toString()), is(equalTo(PlayState.COMPLETED)));
		Assert.assertThat(BuildingCategory.valueOf(BuildingCategory.PRODUCTION.toString()), is(equalTo(BuildingCategory.PRODUCTION)));
	}
	
}
