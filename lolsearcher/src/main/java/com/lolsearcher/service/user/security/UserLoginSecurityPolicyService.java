package com.lolsearcher.service.user.security;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.exception.exception.user.session.SessionException;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.request.user.security.RequestLoginSecurityPolicyDto;
import com.lolsearcher.model.response.front.user.LolsearcherUserDetails;
import com.lolsearcher.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserLoginSecurityPolicyService {

    private final UserRepository userRepository;

    @JpaTransactional
    public int changeLoginSecurityPolicy(RequestLoginSecurityPolicyDto request){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new SessionException("세션이 없음");
        }

        LolsearcherUserDetails userDetails;
        try {
            userDetails = (LolsearcherUserDetails) authentication.getPrincipal();
        } catch (ClassCastException e){
            throw new SessionException("올바른 세션이 아님");
        }

        String userEmail = userDetails.getUsername();
        int requestSecurityLevel = request.getLoginSecurityPolicyLevel();

        LolSearcherUser user = userRepository.findUserByEmail(userEmail);
        int beforeSecurityLevel = user.getSecurityLevel();

        userRepository.updateSecurityLevel(user, requestSecurityLevel);
        userDetails.setSecurityLevel(requestSecurityLevel);

        return beforeSecurityLevel;
    }
}
