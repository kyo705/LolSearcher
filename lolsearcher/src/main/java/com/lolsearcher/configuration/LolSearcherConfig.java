package com.lolsearcher.configuration;

import lombok.RequiredArgsConstructor;
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

	@Value("${lolsearcher.webclient.reactive-server-url}")
	private String reactiveLolSearcherServerUrl;


	@Bean
	public WebClient reactiveLolSearcherWebClient() {
		return webclientBuilder
				.baseUrl(reactiveLolSearcherServerUrl)
				.build();
	}

	@Bean
	public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);

		return jpaTransactionManager;
	}

	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
}
