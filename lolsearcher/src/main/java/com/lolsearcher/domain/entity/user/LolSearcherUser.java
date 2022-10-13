package com.lolsearcher.domain.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LolSearcherUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(unique = true)
	private String username;
	private String password;
	private String role;
	private String email;
	private long lastLoginTimeStamp;
	
	public LolSearcherUser() {}
	
	public LolSearcherUser(String username, String password, String role, String email, long lastLoginTimeStamp) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.email = email;
		this.lastLoginTimeStamp = lastLoginTimeStamp;
	}

	public long getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getLastLoginTimeStamp() {
		return lastLoginTimeStamp;
	}
	public void setLastLoginTimeStamp(long lastLoginTimeStamp) {
		this.lastLoginTimeStamp = lastLoginTimeStamp;
	}
	
	
	
}
