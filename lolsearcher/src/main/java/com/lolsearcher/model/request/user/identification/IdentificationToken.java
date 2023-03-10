package com.lolsearcher.model.request.user.identification;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class IdentificationToken implements Authentication {


    private final Object userInfo;
    private final Integer certificationNum;
    private final Integer requestCertificationNum;

    public IdentificationToken(Object userInfo, Integer certificationNum, Integer requestCertificationNumber) {

        this.userInfo = userInfo;
        this.certificationNum = certificationNum;
        this.requestCertificationNum = requestCertificationNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Integer getCredentials() {
        return requestCertificationNum;
    }

    @Override
    public Integer getDetails() {
        return certificationNum;
    }

    @Override
    public Object getPrincipal() {
        return userInfo;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return null;
    }
}
