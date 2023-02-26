package library.idgenerator.aop;

import library.idgenerator.IdGenerator;
import library.idgenerator.IdGenerators;
import library.idgenerator.annotation.IdGeneration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Aspect
public class IdGenerationAspect {

    private final IdGenerators idGenerators;

    /**
     *
     *  {@link IdGeneration} 이 붙은 메소드가 실행될 때,
     *  해당 메소드의 파라미터에 Entity가 있는지 확인하고 Id 필드 값에 {@link IdGenerator} 를 사용해 주입하고
     *  실제 메소드를 실행시킴
     *
     * @param joinPoint
     * @return Real Method Result
     * @throws Throwable
     */
    @Around("@annotation(library.idgenerator.annotation.IdGeneration)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();

        Queue<Object> queue = new LinkedList<>(List.of(args));

        while(!queue.isEmpty()){
            Object instance = queue.poll();

            Class clazz = instance.getClass();

            if(!clazz.isAnnotationPresent(Entity.class)){
                log.info("클래스 : {} 는 Entity 객체가 아님", instance.getClass().getTypeName());
                continue;
            }
            for(Field field : clazz.getDeclaredFields()){
                if(field.isAnnotationPresent(Id.class)){
                    generateId(instance, field);
                }
                if(isValidOneToMany(field)){
                    Collection children = (Collection) field.get(instance);
                    queue.addAll(children);
                }
                if(isValidOneToOne(field)){
                    Object child = field.get(instance);
                    queue.add(child);
                }
            }
        }
        return joinPoint.proceed(args);
    }

    private void generateId(Object arg, Field idField) {

        try {
            idField.setAccessible(true);

            if(idField.getLong(arg) != 0){
                log.error("Id 필드에 이미 값이 존재함");
                return;
            }
            String entityName = arg.getClass().getTypeName();
            IdGenerator idGenerator = findIdGenerator(entityName);

            long id = idGenerator.generateId();

            idField.set(arg, id);
            idField.setAccessible(false);

        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private boolean isValidOneToOne(Field field) {

        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        if(oneToOne == null){
            return false;
        }
        Set<CascadeType> cascadeTypes = Arrays.stream(oneToOne.cascade()).collect(Collectors.toSet());

        if(oneToOne.mappedBy().equals("")){
            return false;
        }
        return cascadeTypes.contains(CascadeType.ALL) ||
                cascadeTypes.contains(CascadeType.PERSIST) ||
                cascadeTypes.contains(CascadeType.MERGE);
    }

    private boolean isValidOneToMany(Field field) {

        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if(oneToMany == null){
            return false;
        }
        Set<CascadeType> cascadeTypes = Arrays.stream(oneToMany.cascade()).collect(Collectors.toSet());

        return cascadeTypes.contains(CascadeType.ALL) ||
                cascadeTypes.contains(CascadeType.PERSIST) ||
                cascadeTypes.contains(CascadeType.MERGE);
    }

    private IdGenerator findIdGenerator(String entityName) {

        if(idGenerators.size() == 1){
            return idGenerators.getFirstIdGenerator();
        } else {
            return idGenerators.getIdGenerator(entityName);
        }
    }
}
