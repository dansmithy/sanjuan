package com.github.dansmithy.sanjuan.security.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SanJuanRole {

	private SanJuanRole() {}
	
	public static final String PLAYER = "player";
	public static final String ADMIN = "admin";
	

	public static boolean isAdminUser(UserDetails userDetails) {
		for (GrantedAuthority authority : userDetails.getAuthorities()) {
			if (ADMIN.equals(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
}
