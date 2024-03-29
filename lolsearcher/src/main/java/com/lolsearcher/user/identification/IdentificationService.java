package com.lolsearcher.user.identification;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.errors.exception.user.InvalidUserRoleException;
import com.lolsearcher.errors.exception.user.NotExistingUserException;
import com.lolsearcher.login.LolsearcherUserDetails;
import com.lolsearcher.notification.NotificationDevice;
import com.lolsearcher.notification.NotificationService;
import com.lolsearcher.notification.RequestIdentificationNotificationDto;
import com.lolsearcher.user.*;
import com.lolsearcher.utils.RandomCodeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.lolsearcher.notification.NotificationConstant.IDENTIFICATION_SUBJECT;
import static com.lolsearcher.user.Role.USER;
import static com.lolsearcher.user.identification.IdentificationConstant.IDENTIFICATION_NUMBER_SIZE;
import static com.lolsearcher.utils.ResponseDtoFactory.getUserDto;

@RequiredArgsConstructor
@Service
public class IdentificationService {

    private final NotificationService notificationService;
    private final IdentificationRepository identificationRepository;
    private final UserRepository userRepository;

    @Value("${lolsearcher.notification.auth}")
    private Long MASTER_USER_ID;

    @JpaTransactional
    public void create(Long userId, IdentificationRequest request) {

        NotificationDevice device = request.getDevice();
        String identificationCode = RandomCodeUtils.create(IDENTIFICATION_NUMBER_SIZE);
        RequestIdentificationNotificationDto contents = new RequestIdentificationNotificationDto(identificationCode);

        notificationService.sendNotificationMessage(device, MASTER_USER_ID, List.of(userId), IDENTIFICATION_SUBJECT, contents);
        identificationRepository.save(userId, identificationCode);
    }

    @JpaTransactional
    public UserDto identify(Long userId, String requestCode) {

        String identificationCode = identificationRepository.find(userId);
        if(identificationCode == null){
            throw new IllegalArgumentException("request userId does not have identification code");
        }
        if(!identificationCode.equals(requestCode)) {
            throw new IllegalArgumentException("request number is not correct with identification code");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotExistingUserException(userId, 1));
        if(user.getRole() != Role.TEMPORARY){
            throw new InvalidUserRoleException(user, "request user's role must be TEMPORARY");
        }
        userRepository.updateUser(user, UserUpdateRequest.builder().role(USER).build());
        identificationRepository.delete(userId);

        //세션이 있을 경우 authentication 객체의 user details의 authority ROLE 변경
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getDetails() != null) {
            LolsearcherUserDetails details = (LolsearcherUserDetails) authentication.getPrincipal();

            Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
            authorities.clear();
            authorities.add(USER::getValue);

            Collection<GrantedAuthority> authorities2 = (Collection<GrantedAuthority>) details.getAuthorities();
            authorities2.clear();
            authorities2.add(USER::getValue);
        }

        return getUserDto(user);
    }
}
