package com.github.dansmithy.sanjuan.rest.beans;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.dao.UserDao;
import com.github.dansmithy.sanjuan.exception.ImmutableDataException;
import com.github.dansmithy.sanjuan.model.User;
import com.github.dansmithy.sanjuan.model.output.Users;
import com.github.dansmithy.sanjuan.rest.jaxrs.UserResource;

@Named
public class UserBean implements UserResource {

	private UserDao userDao;
	
	@Inject
	public UserBean(UserDao userDao) {
		super();
		this.userDao = userDao;
	}

	@Override
	public User createUser(User user) {
		if (user.getRoles().length == 0) {
			user.setRoles(new String[] { "player" });
		}
		userDao.createUser(user);
		return user;
	}

	@Override
	public User updateUser(String username, User user) {
		if (!username.equals(user.getUsername())) {
			throw new ImmutableDataException(String.format("Cannot change username for user %s", username));
		}
		userDao.updateUser(user);
		return user;
	}

	@Override
	public void deleteUser(String username) {
		userDao.removeUser(username);

	}

	@Override
	public Users getUsers() {
		return new Users(userDao.getUsers());
	}

}
