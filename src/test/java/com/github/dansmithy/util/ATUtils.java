package com.github.dansmithy.util;

import com.github.dansmithy.sanjuan.twitter.service.scribe.ConfigurableTwitterApi;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ATUtils {

    private static final Pattern p = Pattern.compile("http://.*?(\\d+)(/.*){0,1}");
    public static int getRestDriverPort() {
        return Integer.parseInt(extractRestDriverPort(ConfigurableTwitterApi.initializeTwitterBaseUrl()));
    } 
    
    private static String extractRestDriverPort(String url) {
        Matcher m = p.matcher(url);
        if (m.matches()) {
            return m.group(1);
        }
        throw new IllegalArgumentException(String.format("Unable to extra port from url [%s]", url));
    }

    @Test
    public void testCantExtractRestDriverPort() {
        Assert.assertThat(extractRestDriverPort("http://localhost:1234"), is(equalTo("1234")));
        Assert.assertThat(extractRestDriverPort("http://localhost:1234/"), is(equalTo("1234")));
        Assert.assertThat(extractRestDriverPort("http://localhost:1234/other"), is(equalTo("1234")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotExtractRestDriverPortExtract1() {
        extractRestDriverPort("http://localhost");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotExtractRestDriverPortExtract2() {
        extractRestDriverPort("http://localhost:1234andtext");
    }

}
