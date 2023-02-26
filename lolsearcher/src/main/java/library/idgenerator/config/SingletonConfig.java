package library.idgenerator.config;

import library.idgenerator.IdGenerator;
import library.idgenerator.IdGenerators;
import library.idgenerator.aop.IdGenerationAspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SingletonConfig {

    @Value("${lolsearcher.nodeId}")
    private long nodeId;

    @Bean
    public IdGenerators idGenerator(){

        IdGenerators idGenerators = new IdGenerators();
        idGenerators.addIdGenerator("publicIdGenerator", new IdGenerator(nodeId));

        return idGenerators;
    }

    @Bean
    public IdGenerationAspect idGenerationAspect(IdGenerators idGenerators){

        return new IdGenerationAspect(idGenerators);
    }
}
