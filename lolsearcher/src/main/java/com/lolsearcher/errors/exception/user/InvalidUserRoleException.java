package com.lolsearcher.errors.exception.user;

import com.lolsearcher.user.User;
import lombok.Getter;

@Getter
public class InvalidUserRoleException extends IllegalStateException {

    private final User user;

    public InvalidUserRoleException(User user, String message) {
        super(message);
        this.user = user;
    }

    public Long getUserId() {

        return this.user.getId();
    }
}
