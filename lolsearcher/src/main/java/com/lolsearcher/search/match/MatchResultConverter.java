package com.lolsearcher.search.match;

import javax.persistence.AttributeConverter;

public class MatchResultConverter implements AttributeConverter<MatchResultState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(MatchResultState attribute) {

        return attribute.getCode();
    }

    @Override
    public MatchResultState convertToEntityAttribute(Integer dbData) {

        return MatchResultState.valueOfCode(dbData);
    }
}
