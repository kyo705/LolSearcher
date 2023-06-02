package com.lolsearcher.user;

import com.lolsearcher.user.LoginSecurityState.LoginSecurityConverter;
import com.lolsearcher.user.Role.RoleConverter;
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
	@Convert(converter = RoleConverter.class)
	private Role role;
	@Convert(converter = LoginSecurityConverter.class)
	private LoginSecurityState loginSecurity;
	private LocalDateTime lastLoginTimeStamp;


}
