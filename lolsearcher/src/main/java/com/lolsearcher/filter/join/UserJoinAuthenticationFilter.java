package com.lolsearcher.filter.join;

import com.lolsearcher.model.entity.user.LolSearcherUser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.lolsearcher.constant.UriConstants.JOIN_AUTHENTICATION_URI;

@Slf4j
public abstract class UserJoinAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();

        if(!uri.equals(JOIN_AUTHENTICATION_URI)){
            chain.doFilter(request, response);
            return;
        }
        try{
            LolSearcherUser user = attemptAuthentication(request, response);
            successIdentification(request, response, user);
        }catch (Exception e){
            failIdentification(request, response, e);
        }
    }

    protected abstract LolSearcherUser attemptAuthentication(ServletRequest request, ServletResponse response);

    protected abstract void successIdentification(ServletRequest request, ServletResponse response, LolSearcherUser user);

    protected abstract void failIdentification(ServletRequest request, ServletResponse response, Exception e);
}
