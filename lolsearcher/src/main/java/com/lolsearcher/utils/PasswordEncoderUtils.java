package com.lolsearcher.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderUtils {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public static String encodingPassword(String password) {

        return ENCODER.encode(password);
    }

    public static boolean match(String rawPassword, String encodedPassword) {

        return ENCODER.matches(rawPassword, encodedPassword);
    }
}
