package library.idgenerator.config;

import library.idgenerator.IdGenerator;
import library.idgenerator.IdGenerators;
import library.idgenerator.aop.IdGenerationAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.Entity;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class IndividualConfig {

    @Value("${lolsearcher.nodeId}")
    private long nodeId;
    @Value("${id-generator.class-path}")
    private String packageName;

    @Bean
    public IdGenerators idGenerators() throws ClassNotFoundException {

        String directoryName = ClassLoader.getSystemClassLoader()
                .getResource(packageName.replaceAll("[.]", "/")).getFile();

        List<Class> entityClasses = findEntityClass(directoryName, new ArrayList<>());

        IdGenerators idGenerators = new IdGenerators();
        entityClasses.forEach(entityClass->idGenerators.addIdGenerator(entityClass.getTypeName(), new IdGenerator(nodeId)));
        log.info("생성된 IdGenerator 갯수 : {}", idGenerators.size());

        return idGenerators;
    }

    @Bean
    public IdGenerationAspect idGenerationAspect(IdGenerators idGenerators){

        return new IdGenerationAspect(idGenerators);
    }

    private List<Class> findEntityClass(String directoryName, List<Class> classes) throws ClassNotFoundException {

        File file = new File(directoryName);

        if(file.isFile()){
            if(!file.getName().endsWith(".class")){
                return classes;
            }

            String path = directoryName.replaceAll("/",".").substring(0, directoryName.indexOf(".class"));
            int startIdx = path.indexOf(packageName);
            path = path.substring(startIdx, path.length());
            Class clazz = Class.forName(path);

            if(!clazz.isAnnotationPresent(Entity.class)){
                return classes;
            }
            classes.add(clazz);
            return classes;
        }

        Arrays.stream(file.list())
                .forEach(fileName -> {
                    try {
                        findEntityClass(directoryName + "/" + fileName, classes);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });

        return classes;
    }
}
