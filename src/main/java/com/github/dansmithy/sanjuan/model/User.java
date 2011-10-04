package com.github.dansmithy.sanjuan.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;


public class User {

	private String username;
	private String hashedPassword;
	@Transient
	private String password;
	private String[] roles = new String[0];
	@Id
	private ObjectId id;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@JsonIgnore
	public String getHashedPassword() {
		return hashedPassword;
	}
	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
	
	public void setPassword(String password) {
		this.password = password;
		this.hashedPassword = encrypt(password);
	}
	public String[] getRoles() {
		return roles;
	}
	public void setRoles(String[] roles) {
		this.roles = roles;
	}
	
	private String encrypt(String password) {
		return DigestUtils.md5Hex(password);
	}	
		
	
}
