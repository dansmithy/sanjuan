package com.github.dansmithy.sanjuan.game.roles;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Test;

import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.Role;

public class SkipAwareRoleProcessorTest {

	@Test
	public void ensureCoverageGetRoleTest() {
		
		// given
		RoleProcessor mockRoleProcessor = mock(RoleProcessor.class);
		when(mockRoleProcessor.getRole()).thenReturn(Role.BUILDER);
		RoleProcessor roleProcessorUnderTest = new SkipAwareRoleProcessor(mockRoleProcessor);
		
		// when
		Role actualRole = roleProcessorUnderTest.getRole();
		
		// then
		Assert.assertThat(actualRole, is(equalTo(Role.BUILDER)));
		
		
	}
}
