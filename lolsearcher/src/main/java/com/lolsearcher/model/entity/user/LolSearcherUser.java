package com.lolsearcher.model.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@Data
@Entity
@Table(indexes = {@Index(columnList = "email", unique = true)})
public class LolSearcherUser {

	@Id
	private long id;
	private String email;
	private String password;
	private String username;
	private String role;
	private long lastLoginTimeStamp;
	
	public LolSearcherUser(String username, String password, String role, String email, long lastLoginTimeStamp) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.email = email;
		this.lastLoginTimeStamp = lastLoginTimeStamp;
	}
}
