package com.lolsearcher.errors.exception.user.join;

public class ExistedUserException extends RuntimeException {

    private final String email;

    public ExistedUserException(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return String.format("이메일 : %s 는 이미 사용되고 있습니다.", email);
    }
}
