package com.lolsearcher.validation;

import com.lolsearcher.search.champion.PositionState;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositionValidator.class)
public @interface Position {

    String message() default "this position is not permitted";
    Class[] groups() default {};
    Class[] payload() default {};
    PositionState[] anyOf() default {};
}
