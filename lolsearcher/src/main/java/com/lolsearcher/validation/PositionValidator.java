package com.lolsearcher.validation;

import com.lolsearcher.search.champion.PositionState;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PositionValidator implements ConstraintValidator<Position, PositionState> {

    private Set<PositionState> set;

    @Override
    public void initialize(Position constraintAnnotation) {

        set = Stream.of(constraintAnnotation.anyOf())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(PositionState value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }
        return set.contains(value);
    }
}
