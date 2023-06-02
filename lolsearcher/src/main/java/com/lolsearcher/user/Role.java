package com.lolsearcher.user;

import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
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

    private static final Map<String, Role> BY_VALUE =
            Stream.of(values()).collect(Collectors.toMap(Role::getValue, e -> e));

    public static Role of(String value){

        try {
            return BY_VALUE.get(value);
        }catch (NullPointerException e) {
            throw new IllegalArgumentException("Role must be in boundary");
        }

    }

    @Converter
    static class RoleConverter implements AttributeConverter<Role, String> {

        @Override
        public String convertToDatabaseColumn(Role attribute) {
            return attribute.getValue();
        }

        @Override
        public Role convertToEntityAttribute(String dbData) {
            return of(dbData);
        }
    }
}
