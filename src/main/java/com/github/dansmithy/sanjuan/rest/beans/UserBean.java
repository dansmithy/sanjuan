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
	public void deleteUser(String username) {
		userDao.removeUser(username);

	}

	@Override
	public Users getUsers() {
		return new Users(userDao.getUsers());
	}

}
