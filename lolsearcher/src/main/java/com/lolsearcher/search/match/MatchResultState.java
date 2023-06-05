package com.lolsearcher.search.match;

import lombok.Getter;

import javax.persistence.AttributeConverter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum MatchResultState {

    WIN("wins", 0),
    LOSS("losses", 1),
    DRAW("draw", 2);

    private final String name;
    private final int code;

    MatchResultState(String name, int code) {
        this.name = name;
        this.code = code;
    }

    private static final Map<Integer, MatchResultState> BY_NUMBER =
            Stream.of(values()).collect(Collectors.toMap(MatchResultState::getCode, e -> e));

    public static MatchResultState valueOfCode(int code){
        return BY_NUMBER.get(code);
    }

    public static class MatchResultConverter implements AttributeConverter<MatchResultState, Integer> {

        @Override
        public Integer convertToDatabaseColumn(MatchResultState attribute) {

            return attribute.getCode();
        }

        @Override
        public MatchResultState convertToEntityAttribute(Integer dbData) {

            return MatchResultState.valueOfCode(dbData);
        }
    }
}
