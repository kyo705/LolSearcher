package com.lolsearcher.configuration.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FailMatchIdProducerConfig {

    @Value("lolsearcher.kafka.producers.fail_match_id.bootstrap_server")
    private String BOOTSTRAP_SERVER;
    @Value("lolsearcher.kafka.producers.fail_match_id.ack")
    private String ACK;
    @Value("lolsearcher.kafka.producers.fail_match_id.transaction_id")
    private String TRANSACTION_ID;

    @Bean
    public KafkaTemplate<String, String> failMatchIdKafkaTemplate(){
        return new KafkaTemplate<>(failMatchIdProducerFactory());
    }

    @Bean
    public KafkaTransactionManager<String, String> failMatchIdKafkaTransactionManager(){
        return new KafkaTransactionManager<>(failMatchIdProducerFactory());
    }

    @Bean
    public DefaultKafkaProducerFactory<String, String> failMatchIdProducerFactory(){

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, ACK);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TRANSACTION_ID);

        return new DefaultKafkaProducerFactory<>(props);
    }
}
