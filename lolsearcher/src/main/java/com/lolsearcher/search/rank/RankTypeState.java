package com.lolsearcher.search.rank;

import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum RankTypeState {

    RANKED_FLEX_SR(1),
    RANKED_SOLO_5x5(2);

    private final int id;

    RankTypeState(int id) {
        this.id = id;
    }

    private static final Map<Integer, RankTypeState> BY_NUMBER =
            Stream.of(values()).collect(Collectors.toMap(RankTypeState::getId, e -> e));

    public static RankTypeState valueOfId(int code){
        return BY_NUMBER.get(code);
    }


    @Converter
    public static class RankTypeConverter implements AttributeConverter<RankTypeState, Integer> {

        @Override
        public Integer convertToDatabaseColumn(RankTypeState attribute) {

            return attribute.getId();
        }

        @Override
        public RankTypeState convertToEntityAttribute(Integer dbData) {

            return valueOfId(dbData);
        }
    }
}
