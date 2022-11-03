package com.lolsearcher.exception.login;

import org.springframework.security.core.AuthenticationException;

public class IpBannedException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public IpBannedException(String msg) {
		super(msg);
	}

	public IpBannedException(String msg,  Throwable cause) {
		super(msg, cause);
	}
}
