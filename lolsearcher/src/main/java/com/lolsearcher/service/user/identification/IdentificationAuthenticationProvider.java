package com.lolsearcher.service.user.identification;

import com.lolsearcher.exception.exception.user.identification.IdentificationException;
import com.lolsearcher.exception.exception.user.login.IdentificationAuthenticationException;
import com.lolsearcher.model.request.user.identification.IdentificationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class IdentificationAuthenticationProvider implements AuthenticationProvider {

    private final AbstractIdentificationService identificationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        try {
            return identificationService.authenticate(authentication);
        }catch (IdentificationException e){
            throw new IdentificationAuthenticationException(e.getStatusCode(), e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(IdentificationToken.class);
    }
}
