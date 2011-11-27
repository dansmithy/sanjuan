package com.github.dansmithy.sanjuan.model.output;

import java.util.List;

import com.github.dansmithy.sanjuan.model.User;

public class Users {

	private List<User> users;

	public Users(List<User> users) {
		super();
		this.users = users;
	}

	public List<User> getUsers() {
		return users;
	}
	
}
