package com.lolsearcher.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "USERS", indexes = {@Index(columnList = "email", unique = true)})
public class User implements Serializable {

	@Id
	private long id;
	private String email;
	private String password;
	private String username;
	@Enumerated(EnumType.STRING)
	private Role role;
	@Enumerated(EnumType.STRING)
	private LoginSecurityState loginSecurity;
	private LocalDateTime lastLoginTimeStamp;


}
