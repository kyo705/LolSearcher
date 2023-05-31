package com.lolsearcher.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Optional;

import static com.lolsearcher.user.UserConstant.EMAIL_REGEX;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserUpdateRequest {

    @Size(min = 1, max = 20)
    private String name;
    @Email(regexp = EMAIL_REGEX)
    private String email;
    @Size(min = 9)
    private String password;
    private Role role;
    private LoginSecurityState loginSecurity;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public Optional<Role> getRole() {
        return Optional.ofNullable(role);
    }

    public Optional<LoginSecurityState> getLoginSecurity() {
        return Optional.ofNullable(loginSecurity);
    }
}
