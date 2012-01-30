package com.github.dansmithy.sanjuan.rest.beans;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.dao.UserDao;
import com.github.dansmithy.sanjuan.exception.RequestInvalidRuntimeException;
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
		if (user.getUsername() != null && !username.equals(user.getUsername())) {
			throw new RequestInvalidRuntimeException(String.format("Username cannot be updated and should not be specified in the request body."));
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
