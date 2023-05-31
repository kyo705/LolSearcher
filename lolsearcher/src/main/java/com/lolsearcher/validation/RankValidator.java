package com.lolsearcher.validation;

import com.lolsearcher.search.rank.RankTypeState;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RankValidator implements ConstraintValidator<Rank, RankTypeState> {

    private Set<RankTypeState> set;

    @Override
    public void initialize(Rank constraintAnnotation) {

        set = Stream.of(constraintAnnotation.anyOf())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(RankTypeState value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }
        return set.contains(value);
    }
}
