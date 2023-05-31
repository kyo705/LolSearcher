package com.lolsearcher.user;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Role {

    TEMPORARY("ROLE_TEMPORARY"),
    USER("ROLE_USER");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    private static final Map<String, Role> BY_VALUE =
            Stream.of(values()).collect(Collectors.toMap(Role::getValue, e -> e));

    public static Role of(String value){

        return BY_VALUE.get(value);
    }
}
