package com.lolsearcher.config.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static com.lolsearcher.constant.UriConstants.LOLSEARCHER_FRONT_SERVER_URI;

@Configuration
public class CorsConfig {

	@Bean
	@Qualifier("openApiCorsFilter")
	CorsFilter oepnApiCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowCredentials(true);
		corsConfig.addAllowedOrigin(CorsConfiguration.ALL);
		corsConfig.addAllowedMethod(HttpMethod.GET);
		corsConfig.addAllowedHeader(CorsConfiguration.ALL);
		
		source.registerCorsConfiguration("/api/**", corsConfig);
		
		return new CorsFilter(source);
	}

	@Bean
	@Qualifier("lolSearcherCorsFilter")
	CorsFilter lolSearcherCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowCredentials(true);
		corsConfig.addAllowedOrigin(LOLSEARCHER_FRONT_SERVER_URI);
		corsConfig.addAllowedMethod(HttpMethod.GET);
		corsConfig.addAllowedMethod(HttpMethod.POST);
		corsConfig.addAllowedHeader(CorsConfiguration.ALL);

		source.registerCorsConfiguration("/**", corsConfig);

		return new CorsFilter(source);
	}
}
