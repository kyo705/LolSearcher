package com.lolsearcher.search.match;

import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum TeamState {

    red(0),
    blue(1);

    private final int code;

    TeamState(int code) {
        this.code = code;
    }

    private static final Map<Integer, TeamState> BY_NUMBER =
            Stream.of(values()).collect(Collectors.toMap(TeamState::getCode, e -> e));

    public static TeamState valueOfCode(int code){
        return BY_NUMBER.get(code);
    }

    @Converter
    public static class TeamConverter implements AttributeConverter<TeamState, Integer> {

        @Override
        public Integer convertToDatabaseColumn(TeamState attribute) {
            return attribute.getCode();
        }

        @Override
        public TeamState convertToEntityAttribute(Integer dbData) {
            return TeamState.valueOfCode(dbData);
        }
    }
}
