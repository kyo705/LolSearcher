package com.lolsearcher.model.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@Data
@Entity
@Table(indexes = {@Index(columnList = "email", unique = true)})
public class LolSearcherUser implements Serializable {

	@Id
	private long id;
	private String email;
	private String password;
	private String username;
	private String role;
	private long lastLoginTimeStamp;
	private int securityLevel;

	public LolSearcherUser(){}
}
