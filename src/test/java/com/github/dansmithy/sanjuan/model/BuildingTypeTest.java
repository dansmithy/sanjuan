package com.github.dansmithy.sanjuan.model;

import static org.hamcrest.Matchers.*;

import org.junit.Assert;
import org.junit.Test;

public class BuildingTypeTest {

	@Test
	public void testHashcode() {
		BuildingType type1 = new BuildingType("name", 5, 2, 3, BuildingCategory.VIOLET, 3, "type", "description");
		BuildingType type2 = new BuildingType("name", 5, 2, 3, BuildingCategory.VIOLET, 3, "type", "description");
		Assert.assertThat(type1.hashCode(), is(equalTo(type2.hashCode())));
	}
	
	@Test
	public void testToString() {
		BuildingType type1 = new BuildingType("name", 5, 2, 3, BuildingCategory.VIOLET, 3, "type", "description");
		Assert.assertThat(type1.hashCode(), is(not(nullValue())));
	}
	
}
