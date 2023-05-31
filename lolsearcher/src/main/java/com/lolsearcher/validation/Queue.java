package com.lolsearcher.validation;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = QueueValidator.class)
public @interface Queue {

    String message() default "this queue type is not permitted";
    Class[] groups() default {};
    Class[] payload() default {};
}
