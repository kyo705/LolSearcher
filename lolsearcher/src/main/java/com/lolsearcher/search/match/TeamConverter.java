package com.lolsearcher.search.match;

import javax.persistence.AttributeConverter;

public class TeamConverter implements AttributeConverter<TeamState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TeamState attribute) {
        return attribute.getCode();
    }

    @Override
    public TeamState convertToEntityAttribute(Integer dbData) {
        return TeamState.valueOfCode(dbData);
    }
}
