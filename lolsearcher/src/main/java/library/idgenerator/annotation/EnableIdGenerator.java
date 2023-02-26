package library.idgenerator.annotation;


import library.idgenerator.config.IdGeneratorConfigSelector;
import library.idgenerator.enumeration.IdGenerationMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(IdGeneratorConfigSelector.class)
public @interface EnableIdGenerator {

    IdGenerationMode mode() default IdGenerationMode.INDIVIDUAL;
}
