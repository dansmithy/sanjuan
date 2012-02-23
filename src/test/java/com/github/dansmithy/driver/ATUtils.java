package com.github.dansmithy.driver;

import com.github.dansmithy.sanjuan.twitter.service.scribe.ConfigurableTwitterApi;

public class ATUtils {

    public static int getRestDriverPort() {
        return Integer.parseInt(ConfigurableTwitterApi.getRestDriverPort());
    } 
}
