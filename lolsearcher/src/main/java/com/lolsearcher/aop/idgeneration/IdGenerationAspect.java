package com.lolsearcher.aop.idgeneration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Field;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class IdGenerationAspect {

    private final IdGenerator idGenerator;

    @Around("@annotation(IdGenerator)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();

        for(Object arg : args){

            if(arg.getClass().getAnnotation(Entity.class) == null){
                continue;
            }
            try {
                Field idField = null;

                for(Field field : arg.getClass().getDeclaredFields()){
                    if(field.isAnnotationPresent(Id.class)){
                        idField = field;
                        break;
                    }
                }
                if(idField == null){
                    log.error("파라미터에 Entity 객체가 존재하지 않음");
                    continue;
                }

                idField.setAccessible(true);
                try{
                    if(idField.getLong(arg) != 0){
                        log.error("Id 필드에 이미 값이 존재함");
                        continue;
                    }
                    log.info("Id 필드에 값이 존재하지 않음");
                }catch (NullPointerException e){
                    log.info("Id 필드에 값이 존재하지 않음");
                }

                long id = idGenerator.generateId(); //idGenerator 로 생성

                idField.set(arg, id);
                idField.setAccessible(false);

            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return joinPoint.proceed(args);
    }
}
