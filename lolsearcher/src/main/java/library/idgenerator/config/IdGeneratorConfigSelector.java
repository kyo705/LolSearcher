package library.idgenerator.config;

import library.idgenerator.annotation.EnableIdGenerator;
import library.idgenerator.enumeration.IdGenerationMode;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

public class IdGeneratorConfigSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {

        Map<String, Object> attributeMap =
                importingClassMetadata.getAnnotationAttributes(EnableIdGenerator.class.getName(), false);

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(attributeMap);

        assert attributes != null;
        IdGenerationMode mode = attributes.getEnum("mode");

        if(mode == IdGenerationMode.INDIVIDUAL){
            return new String[]{IndividualConfig.class.getName()};
        }else if(mode == IdGenerationMode.SINGLETON){
            return new String[]{SingletonConfig.class.getName()};
        }
        return new String[0];
    }
}
