package com.lolsearcher.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lolsearcher.filter.EncodingFilter;
import com.lolsearcher.filter.IpBanFilter;
import com.lolsearcher.filter.parameter.ChampionValidationFilter;
import com.lolsearcher.filter.parameter.PositionValidationFilter;
import com.lolsearcher.filter.parameter.SummonerNameValidationFilter;

@Configuration
public class FilterConfig {

	@Bean
	FilterRegistrationBean<EncodingFilter> encodingFilter(){
		FilterRegistrationBean<EncodingFilter> bean = 
				new FilterRegistrationBean<>(new EncodingFilter());
		
		bean.addUrlPatterns("/*");
		bean.setOrder(0);
		
		return bean;
	}
	
	@Bean
	FilterRegistrationBean<IpBanFilter> ipBanFilter(){
		FilterRegistrationBean<IpBanFilter> bean = 
				new FilterRegistrationBean<>(new IpBanFilter());
		
		bean.addUrlPatterns("/*");
		bean.setOrder(1);
		
		return bean;
	}
	
	@Bean
	FilterRegistrationBean<SummonerNameValidationFilter> summonernameParamFilter(){
		FilterRegistrationBean<SummonerNameValidationFilter> bean = 
				new FilterRegistrationBean<>(new SummonerNameValidationFilter());
		
		bean.addUrlPatterns("/summoner","/ingame");
		bean.setOrder(2);
		
		return bean;
	}
	
	@Bean
	FilterRegistrationBean<PositionValidationFilter> positionParamFilter(){
		FilterRegistrationBean<PositionValidationFilter> bean = 
				new FilterRegistrationBean<>(new PositionValidationFilter());
		
		bean.addUrlPatterns("/champions");
		bean.setOrder(3);
		
		return bean;
	}
	
	@Bean
	FilterRegistrationBean<ChampionValidationFilter> championParamFilter(){
		
		
		FilterRegistrationBean<ChampionValidationFilter> bean = 
				new FilterRegistrationBean<>(new ChampionValidationFilter());
		
		bean.addUrlPatterns("/champions/detail");
		bean.setOrder(4);
		
		return bean;
	}
}
