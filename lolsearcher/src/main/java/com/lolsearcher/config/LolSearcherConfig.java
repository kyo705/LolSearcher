package com.lolsearcher.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.reactive.function.client.WebClient;

import javax.persistence.EntityManagerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Configuration
public class LolSearcherConfig {

	@Value("${lolsearcher.webclient.reactive-server-url}")
	private String REACTIVE_LOL_SEARCHER_SERVER_URI;
	@Value("${lolsearcher.webclient.notification-server-url}")
	private String NOTIFICATION_SERVER_URI;

	private final WebClient.Builder webclientBuilder;

	@Qualifier("reactiveLolSearcherWebClient")
	@Bean
	public WebClient reactiveLolSearcherWebClient() {
		return webclientBuilder
				.baseUrl(REACTIVE_LOL_SEARCHER_SERVER_URI)
				.build();
	}

	@Qualifier("notificationWebClient")
	@Bean
	public WebClient notificationWebClient() {
		return webclientBuilder
				.baseUrl(NOTIFICATION_SERVER_URI)
				.build();
	}

	@Bean
	public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);

		return jpaTransactionManager;
	}

	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
		return new HiddenHttpMethodFilter();
	}

	@Bean
	public ExecutorService threadPool(){
		return Executors.newFixedThreadPool(54);
	}
}
