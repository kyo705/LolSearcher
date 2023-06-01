package com.lolsearcher.search.rank;

import lombok.Getter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum TierState {

    CHALLENGER(1),
    GRANDMASTER(2),
    MASTER(3),
    DIAMOND(4),
    PLATINUM(5),
    GOLD(6),
    SILVER(7),
    BRONZE(8),
    IRON(9),
    UNRANK(10);

    private final int id;

    TierState(int id) {
        this.id = id;
    }

    private static final Map<Integer, TierState> BY_NUMBER =
            Stream.of(values()).collect(Collectors.toMap(TierState::getId, e -> e));

    public static TierState valueOfId(int code){
        return BY_NUMBER.get(code);
    }

    @Converter
    public static class TierConverter implements AttributeConverter<TierState, Integer> {

        @Override
        public Integer convertToDatabaseColumn(TierState attribute) {
            return attribute.getId();
        }

        @Override
        public TierState convertToEntityAttribute(Integer dbData) {
            return TierState.valueOfId(dbData);
        }
    }
}
