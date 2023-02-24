package com.lolsearcher.model.response.front.user;

import com.lolsearcher.model.entity.user.LolSearcherUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class LolsearcherUserDetails implements UserDetails, OAuth2User {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private String username;
	private String password;
	private String usermail;
	private List<GrantedAuthority> roles;
	private long lastLoginTimeStamp;
	private Map<String, Object> attributes;
	
	public LolsearcherUserDetails() {}
	
	public LolsearcherUserDetails(LolSearcherUser user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.lastLoginTimeStamp = user.getLastLoginTimeStamp();
		this.usermail = user.getEmail();
		
		roles = new ArrayList<>();
		roles.add(new GrantedAuthority() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
	}
	
	public LolsearcherUserDetails(LolSearcherUser user, Map<String,Object> attributes) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.lastLoginTimeStamp = user.getLastLoginTimeStamp();
		this.attributes = attributes;
		
		roles = new ArrayList<>();
		roles.add(new GrantedAuthority() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}
	

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public long getLastLoginTimeStamp() {
		return lastLoginTimeStamp;
	}

	public String getUsermail() {
		return usermail;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
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
	public int hashCode() {
		return Objects.hash(id, password, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LolsearcherUserDetails other = (LolsearcherUserDetails) obj;
		return id == other.id && Objects.equals(password, other.password) && Objects.equals(username, other.username);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getName() {
		return (String)this.attributes.get("sub");
	}

	
	
}
