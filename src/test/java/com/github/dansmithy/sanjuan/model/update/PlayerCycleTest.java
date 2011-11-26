package com.github.dansmithy.sanjuan.model.update;

import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class PlayerCycleTest {

	@Test
	public void getListOfPlayers() {
		
		// given
		PlayerCycle cycle = new PlayerCycle(Arrays.asList("one", "two", "three"));
		Iterator<String> iterator = cycle.startAt("two").iterator();
		
		// when
		String allPlayers = StringUtils.join(iterator, ",");
		
		// then
		Assert.assertThat(allPlayers, is(equalTo("two,three,one")));
	}
	
	@Test(expected = IllegalStateException.class)
	public void ensureCoverageTestCannotRemoveWithIterator() {
		
		// given
		PlayerCycle cycle = new PlayerCycle(Arrays.asList("one", "two", "three"));
		Iterator<String> iterator = cycle.startAt("two").iterator();
		
		// when
		iterator.remove();
		
		// then
		// see expected
	}
}
