package com.lolsearcher.user;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.login.LolsearcherUserDetails;
import com.lolsearcher.utils.ResponseDtoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.lolsearcher.user.LoginSecurityState.NONE;
import static com.lolsearcher.user.Role.TEMPORARY;
import static com.lolsearcher.utils.PasswordEncoderUtils.encodingPassword;
import static com.lolsearcher.utils.ResponseDtoFactory.getUserDto;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @JpaTransactional
    public UserDto join(UserCreateRequest request) {

        if(findByEmail(request.getEmail())){
            throw new DataIntegrityViolationException(String.format("email : %s already exist", request.getEmail()));
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(encodingPassword(request.getPassword()))
                .username(request.getUsername())
                .role(TEMPORARY)
                .loginSecurity(NONE)
                .build();

        userRepository.save(user);

        return getUserDto(user);
    }

    @JpaTransactional(readOnly = true)
    public boolean findByEmail(String email) {

        return userRepository.findByEmail(email).isPresent();
    }

    @JpaTransactional(readOnly = true)
    public UserDto findById(Long id) {

        return userRepository.findById(id)
                .map(ResponseDtoFactory::getUserDto)
                .orElse(null);
    }


    @JpaTransactional
    public void updatePartial(Long id, UserUpdateRequest request) {

        checkId(id);
        User user = userRepository.findById(id).orElseThrow();

        userRepository.updateUser(user, request);
    }

    @JpaTransactional
    public void delete(Long id) {

        checkId(id);
        userRepository.delete(id);
    }

    private void checkId(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LolsearcherUserDetails userDetails = (LolsearcherUserDetails) authentication.getPrincipal();
        Long realId = userDetails.getId();

        if(!Objects.equals(id, realId)) {
            throw new IllegalArgumentException(String.format("request id : %s is not same with user's id : %s", id, realId));
        }
    }

}
