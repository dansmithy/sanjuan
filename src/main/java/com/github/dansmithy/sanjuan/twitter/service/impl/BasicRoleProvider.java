package com.github.dansmithy.sanjuan.twitter.service.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.config.ConfigurationStore;
import org.apache.commons.lang.ArrayUtils;

import com.github.dansmithy.sanjuan.twitter.model.TwitterUser;
import com.github.dansmithy.sanjuan.twitter.service.RoleProvider;

@Named
public class BasicRoleProvider implements RoleProvider {

	private final String adminUsername;

    @Inject
	public BasicRoleProvider(ConfigurationStore configurationStore) {
        adminUsername = configurationStore.getAdminUsername();
	}

	@Override
	public String[] getRolesForUser(String username) {
		if (adminUsername.equals(username)) {
			return new String[] { TwitterUser.ROLE_ADMIN };
		}
		return new String[] { TwitterUser.ROLE_PLAYER };
	}

	public String getAdminUsername() {
		return adminUsername;
	}
}
