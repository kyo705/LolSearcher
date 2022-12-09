package com.lolsearcher.model.entity.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
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
	
	public LolSearcherUser(String username, String password, String role, String email, long lastLoginTimeStamp) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.email = email;
		this.lastLoginTimeStamp = lastLoginTimeStamp;
	}
}
