package com.github.dansmithy.sanjuan.util;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class CollectionUtilsTest {

	@Test
	public void ensureCoverage() {
		new CollectionUtils();
	}
	
	@Test
	public void identifyDuplicates() {
		
		// given
		Collection<String> collection = Arrays.asList("one", "two", "one");
		
		// when
		boolean hasDuplicates = CollectionUtils.hasDuplicates(collection);
		
		// then
		Assert.assertTrue("expected to find duplicates", hasDuplicates);
	}
}
