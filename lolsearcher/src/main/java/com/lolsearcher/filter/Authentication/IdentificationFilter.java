package com.lolsearcher.filter.Authentication;

import com.lolsearcher.model.request.user.identification.IdentificationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  본인 인증을 위한 필터
 */
public abstract class IdentificationFilter extends UsernamePasswordAuthenticationFilter {

    private boolean postOnly = true;

    public IdentificationFilter() {
        super();
    }

    public IdentificationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        Object userInfo = obtainUserInfo(request);
        int certificationNumber = obtainCertificationNum(request);
        int requestCertificationNumber = obtainRequestCertificationNum(request);

        Authentication authentication = new IdentificationToken(userInfo, certificationNumber, requestCertificationNumber);

        return this.getAuthenticationManager().authenticate(authentication);
    }

    protected abstract Object obtainUserInfo(HttpServletRequest request);

    protected abstract Integer obtainCertificationNum(HttpServletRequest request);

    protected abstract Integer obtainRequestCertificationNum(HttpServletRequest request);
}
