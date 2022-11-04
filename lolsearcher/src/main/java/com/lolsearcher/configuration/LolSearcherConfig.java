package com.lolsearcher.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.api.riotgames.RiotRestApiv2;
import com.lolsearcher.domain.entity.summoner.match.Match;



@Configuration
public class LolSearcherConfig {

	private WebClient.Builder webclientBuilder;
	
	public LolSearcherConfig(WebClient.Builder webclientBuilder) {
		this.webclientBuilder = webclientBuilder;
	}
	
	@Bean
	public WebClient webclient() {
		return webclientBuilder.build();
	}
	@Bean
	public ExecutorService matchSavingThreadPool() {
		return Executors.newFixedThreadPool(100);
	}
	@Bean
	public RiotRestAPI riotRestApi(WebClient webclient) {
		return new RiotRestApiv2(webclient);
	}
	
	//-------------------- 시큐리티 관련 @bean 등록 ----------------------------
	
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}
	
	//-------------------- 카프카 관련 @bean 등록 ----------------------------
	
	@Bean
	public KafkaTemplate<String, Match> MatchesKafkaTemplate(){
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		props.put(ProducerConfig.ACKS_CONFIG, 1);
		props.put(ProducerConfig.RETRIES_CONFIG, 5);
		
		ProducerFactory<String, Match> producerFactory = new DefaultKafkaProducerFactory<>(props);
		
		return new KafkaTemplate<String, Match>(producerFactory);
	}
	@Bean
	public KafkaTemplate<String, String> failMatchIdsKafkaTemplate(){
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.ACKS_CONFIG, 1);
		props.put(ProducerConfig.RETRIES_CONFIG, 5);
		
		ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(props);
		
		return new KafkaTemplate<String, String>(producerFactory);
	}
	
	
}
