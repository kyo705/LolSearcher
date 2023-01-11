package com.lolsearcher.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.reactive.function.client.WebClient;

import javax.persistence.EntityManagerFactory;

@RequiredArgsConstructor
@Configuration
public class LolSearcherConfig {

	private final WebClient.Builder webclientBuilder;

	@Value("${lolsearcher.webclient.kr-base-url}")
	private String krBaseUrl;

	@Value("${lolsearcher.webclient.asia-base-url}")
	private String asiaBaseUrl;

	@Qualifier("koreaWebClient")
	@Bean
	public WebClient koreaWebClient() {
		return webclientBuilder
				.baseUrl(krBaseUrl)
				.build();
	}

	@Qualifier("asiaWebClient")
	@Bean
	public WebClient asiaWebClient() {
		return webclientBuilder
				.baseUrl(asiaBaseUrl)
				.build();
	}

	@Bean
	public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);

		return jpaTransactionManager;
	}

	@Bean
	public ExecutorService matchSavingThreadPool() {
		return Executors.newFixedThreadPool(54);
	}

	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
}
