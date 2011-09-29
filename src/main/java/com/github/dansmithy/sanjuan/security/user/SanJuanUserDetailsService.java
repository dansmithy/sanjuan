package com.github.dansmithy.sanjuan.security.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.dansmithy.sanjuan.dao.UserDao;
import com.github.dansmithy.sanjuan.model.User;

public class SanJuanUserDetailsService implements UserDetailsService {

	private final UserDao userDao;

	@Autowired
	public SanJuanUserDetailsService(UserDao userDao) {
		this.userDao = userDao;
		
	}
	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userDao.getUser(username);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("User %s not known to system.", username));
		}
		return new SanJuanUserDetails(user);
	}

}
