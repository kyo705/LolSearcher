package com.lolsearcher.validation;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GameVersionValidator.class)
public @interface GameVersion {

    String message() default "gameVersion is invalid";
    Class[] groups() default {};
    Class[] payload() default {};
}
