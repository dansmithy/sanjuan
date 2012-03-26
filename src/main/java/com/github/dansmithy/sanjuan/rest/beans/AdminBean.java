package com.github.dansmithy.sanjuan.rest.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.config.ConfigurationStore;
import org.springframework.beans.factory.annotation.Value;

import com.github.dansmithy.sanjuan.rest.jaxrs.AdminResource;

@Named
public class AdminBean implements AdminResource {

	private String version;
    private final ConfigurationStore configurationStore;

    @Inject
	public AdminBean(@Value("${build.version}") String version, ConfigurationStore configurationStore) {
		super();
        this.configurationStore = configurationStore;
        if ("DEV".equals(version)) {
            this.version = Long.toString(new Date().getTime());
        } else {
            this.version = version;
        }
	}

	@Override
	public Map<String, String> mongoDetails() {
		Map<String, String> map = new HashMap<String, String>();
		String mongoUri = System.getenv("MONGOLAB_URI");
		map.put("type", "mongo");
		map.put("uri", mongoUri);
		return map;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getVersionInJsonFormat() {
		//return String.format("var buildVersion = \"%s\";", version);
		return String.format("angular.service('version', function() { return '%s'; });", version);
	}

    @Override
    public String getTwitterBaseUrl() {
        return configurationStore.getTwitterBaseUrl();
    }

    @Override
    public String getAdminUsername() {
        return configurationStore.getAdminUsername();
    }

}
