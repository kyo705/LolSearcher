package com.lolsearcher.configuration.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FailMatchIdProducerConfig {

    @Value("lolsearcher.kafka.producers.fail_match_id.bootstrap_server")
    private String BOOTSTRAP_SERVER;

    @Bean
    public KafkaTemplate<String, String> failMatchIdKafkaTemplate(){
        return new KafkaTemplate<>(failMatchIdProducerFactory());
    }

    @Bean
    public DefaultKafkaProducerFactory<String, String> failMatchIdProducerFactory(){

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(props);
    }
}
