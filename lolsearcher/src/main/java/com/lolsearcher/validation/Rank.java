package com.lolsearcher.validation;

import com.lolsearcher.search.rank.RankTypeState;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RankValidator.class)
public @interface Rank {

    String message() default "this rank type is not permitted";
    Class[] groups() default {};
    Class[] payload() default {};
    RankTypeState[] anyOf() default {};
}
