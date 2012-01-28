package com.github.dansmithy.sanjuan.security.user;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.dansmithy.sanjuan.model.User;

/**
 * Used mainly for testing purposes, when LDAP is not an option.
 *
 * When not using LDAP, an authenticated user is stored in this form.
 *
 */
public class SanJuanUserDetails implements UserDetails {

	private static final long serialVersionUID = -3339946016096429954L;

	public static final SanJuanUserDetails DISABLED_USER = createDisabledUser();

	private final User user;

	public SanJuanUserDetails(User user) {
		this.user = user;
	}

	private static SanJuanUserDetails createDisabledUser() {
		SanJuanUserDetails disabledUser = new SanJuanUserDetails(new User());
		return disabledUser;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
		for (String role : user.getRoles()) {
			result.add(new GrantedAuthorityImpl(role));
		}
		return result;
	}

	@Override
	public String getPassword() {
		return user.getHashedPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
