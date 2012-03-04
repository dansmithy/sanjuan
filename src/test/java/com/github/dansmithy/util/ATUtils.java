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
    
    private static final String DEFAULT_REST_DRIVER_PORT = "48080";

    public static int extractRestDriverPort(String url) {
        return Integer.parseInt(extractRestDriverPortAsString(url));
    }
    
    private static String extractRestDriverPortAsString(String url) {
        Matcher m = p.matcher(url);
        if (m.matches()) {
            return m.group(1);
        }
        return DEFAULT_REST_DRIVER_PORT;
    }

    @Test
    public void testCantExtractRestDriverPort() {
        Assert.assertThat(extractRestDriverPortAsString("http://localhost:1234"), is(equalTo("1234")));
        Assert.assertThat(extractRestDriverPortAsString("http://localhost:1234/"), is(equalTo("1234")));
        Assert.assertThat(extractRestDriverPortAsString("http://localhost:1234/other"), is(equalTo("1234")));
        Assert.assertThat(extractRestDriverPortAsString("http://localhost"), is(equalTo(DEFAULT_REST_DRIVER_PORT)));
        Assert.assertThat(extractRestDriverPortAsString("http://localhost:1234andtext"), is(equalTo(DEFAULT_REST_DRIVER_PORT)));
    }



}
