package com.github.dansmithy.sanjuan.security.spring;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.web.RedirectStrategy;

import com.github.dansmithy.sanjuan.security.JsonUserDetailsProducer;

public class JsonResponseRedirectStrategyTest {

	@Test
	public void testSendRedirect() throws IOException {
		
		// given
		JsonUserDetailsProducer mockJsonUserDetailsProducer = mock(JsonUserDetailsProducer.class);
		RedirectStrategy redirectStrategy = new JsonResponseRedirectStrategy(mockJsonUserDetailsProducer);
		HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
		CharArrayWriter charArrayWriter = new CharArrayWriter();
		PrintWriter stubPrintWriter = new PrintWriter(charArrayWriter);
		when(mockHttpServletResponse.getWriter()).thenReturn(stubPrintWriter);
		when(mockJsonUserDetailsProducer.generateUserDetailsJson()).thenReturn("testOutput");
		
		// when
		redirectStrategy.sendRedirect(mock(HttpServletRequest.class), mockHttpServletResponse, "url");
		
		// then
		Assert.assertThat(charArrayWriter.toString(), is(equalTo("testOutput")));
	}
}
