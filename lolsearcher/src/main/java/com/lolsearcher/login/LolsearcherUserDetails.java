package com.lolsearcher.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lolsearcher.user.LoginSecurityState;
import com.lolsearcher.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.*;

public class LolsearcherUserDetails implements UserDetails, OAuth2User {

	private static final long serialVersionUID = 1L;
	
	private final long id;
	private final String email;
	private String password;
	private final List<GrantedAuthority> roles;
	private String nickname;
	private LoginSecurityState loginSecurity;
	private final LocalDateTime lastLoginTimestamp;
	private Map<String, Object> attributes;
	
	public LolsearcherUserDetails(User user) {

		this.id = user.getId();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.nickname = user.getUsername();
		this.loginSecurity = user.getLoginSecurity();
		this.lastLoginTimestamp = user.getLastLoginTimeStamp();

		roles = new ArrayList<>();
		roles.add(()->user.getRole().getValue());
	}
	
	public LolsearcherUserDetails(User user, Map<String,Object> attributes) {

		this.id = user.getId();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.nickname = user.getUsername();
		this.attributes = attributes;
		this.loginSecurity = user.getLoginSecurity();
		this.lastLoginTimestamp = user.getLastLoginTimeStamp();

		roles = new ArrayList<>();
		roles.add(()->user.getRole().getValue());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	public long getId(){
		return id;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LoginSecurityState getLoginSecurity(){
		return loginSecurity;
	}

	public void setLoginSecurity(int level){
		setLoginSecurity(LoginSecurityState.valueOfLevel(level));
	}

	public void setLoginSecurity(LoginSecurityState loginSecurity){
		this.loginSecurity = loginSecurity;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public LocalDateTime getLastLoginTimestamp() {
		return lastLoginTimestamp;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@JsonIgnore
	@Override
	public String getName() {
		return (String)this.attributes.get("sub");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LolsearcherUserDetails that = (LolsearcherUserDetails) o;
		return id == that.id && Objects.equals(password, that.password) && Objects.equals(email, that.email);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, password, email);
	}
}
