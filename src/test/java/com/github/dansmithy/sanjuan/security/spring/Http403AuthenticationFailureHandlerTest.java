package com.github.dansmithy.sanjuan.security.spring;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class Http403AuthenticationFailureHandlerTest {

	@Test
	public void testOnAuthenticationFailure() throws IOException, ServletException {
		
		// given
		AuthenticationFailureHandler authenticationFailureHandler = new Http403AuthenticationFailureHandler();
		HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
		CharArrayWriter charArrayWriter = new CharArrayWriter();
		PrintWriter stubPrintWriter = new PrintWriter(charArrayWriter);
		when(mockHttpServletResponse.getWriter()).thenReturn(stubPrintWriter);
		
		// when
		authenticationFailureHandler.onAuthenticationFailure(mock(HttpServletRequest.class), mockHttpServletResponse, null);
		
		// then
		Assert.assertThat(charArrayWriter.toString(), is(equalTo(Http403AuthenticationFailureHandler.ACCESS_DENIED_JSON)));
	}
}
