package com.lolsearcher.model.response.front.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class LolsearcherUserDetails implements UserDetails, OAuth2User {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private String password;
	private String email;
	private int securityLevel;
	private List<GrantedAuthority> roles;
	private Map<String, Object> attributes;
	
	public LolsearcherUserDetails() {}
	
	public LolsearcherUserDetails(LolSearcherUser user) {

		this.id = user.getId();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.securityLevel = user.getSecurityLevel();

		roles = new ArrayList<>();
		roles.add(user::getRole);
	}
	
	public LolsearcherUserDetails(LolSearcherUser user, Map<String,Object> attributes) {

		this.id = user.getId();
		this.password = user.getPassword();
		this.attributes = attributes;
		this.securityLevel = user.getSecurityLevel();
		
		roles = new ArrayList<>();
		roles.add(user::getRole);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	public long getId(){
		return id;
	}
	@Override public String getUsername() {
		return email;
	}

	@Override public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getSecurityLevel(){
		return securityLevel;
	}
	public void setSecurityLevel(int level){
		this.securityLevel = level;
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
