package com.lolsearcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class LolsearcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(LolsearcherApplication.class, args);
	}

}
