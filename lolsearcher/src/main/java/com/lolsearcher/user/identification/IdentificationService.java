package com.lolsearcher.user.identification;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.login.LolsearcherUserDetails;
import com.lolsearcher.notification.NotificationDevice;
import com.lolsearcher.notification.NotificationService;
import com.lolsearcher.user.User;
import com.lolsearcher.user.UserDto;
import com.lolsearcher.user.UserRepository;
import com.lolsearcher.user.UserUpdateRequest;
import com.lolsearcher.utils.RandomNumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.lolsearcher.user.Role.USER;
import static com.lolsearcher.utils.factory.FrontServerResponseDtoFactory.getUserDto;

@RequiredArgsConstructor
@Service
public class IdentificationService {

    private final NotificationService notificationService;
    private final IdentificationRepository identificationRepository;
    private final UserRepository userRepository;

    @JpaTransactional
    public void create(Long userId, IdentificationRequest request) {

        NotificationDevice device = request.getDevice();
        String deviceValue = request.getDeviceValue();

        String identificationNum = RandomNumberUtils.create(IdentificationConstant.IDENTIFICATION_NUMBER_SIZE);

        notificationService.sendIdentificationMessage(device, deviceValue, identificationNum);

        identificationRepository.save(userId, identificationNum);

    }

    @JpaTransactional
    public UserDto identify(Long userId, String requestNum) {

        String identificationNum = identificationRepository.find(userId);

        if(!identificationNum.equals(requestNum)) {
            throw new IllegalArgumentException("request number is not correct with identification number");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new EmptyResultDataAccessException(1));

        userRepository.updateUser(user, UserUpdateRequest.builder().role(USER).build());
        identificationRepository.delete(userId);

        //세션이 있을 경우 authentication 객체의 user details의 authority ROLE 변경
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
            authorities.clear();
            authorities.add(USER::getValue);

            LolsearcherUserDetails details = (LolsearcherUserDetails) authentication.getPrincipal();
            Collection<GrantedAuthority> authorities2 = (Collection<GrantedAuthority>) details.getAuthorities();
            authorities2.clear();
            authorities2.add(USER::getValue);
        }

        return getUserDto(user);
    }
}
