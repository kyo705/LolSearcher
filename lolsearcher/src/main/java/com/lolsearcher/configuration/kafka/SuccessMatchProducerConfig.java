package com.lolsearcher.configuration.kafka;

import com.lolsearcher.model.entity.match.Match;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SuccessMatchProducerConfig {

    @Value("lolsearcher.kafka.producers.success_match.bootstrap_server")
    private String BOOTSTRAP_SERVER;
    @Value("lolsearcher.kafka.producers.success_match.ack")
    private String ACK;
    @Value("lolsearcher.kafka.producers.success_match.idempotence")
    private Boolean ENABLE_IDEMPOTENCE;
    @Value("lolsearcher.kafka.producers.success_match.transaction_id")
    private String TRANSACTION_ID;

    @Bean
    public KafkaTemplate<String, Match> successMatchKafkaTemplate(){
        return new KafkaTemplate<>(successMatchesProducerFactory());
    }

    @Bean
    public KafkaTransactionManager<String, Match> successMatchKafkaTransactionManager(){
        return new KafkaTransactionManager<>(successMatchesProducerFactory());
    }

    @Bean
    public DefaultKafkaProducerFactory<String, Match> successMatchesProducerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, ACK);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, ENABLE_IDEMPOTENCE);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TRANSACTION_ID);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, CompressionType.ZSTD.name);

        return new DefaultKafkaProducerFactory<>(props);
    }
}
