package com.lolsearcher;

import library.idgenerator.annotation.EnableIdGenerator;
import library.idgenerator.enumeration.IdGenerationMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableIdGenerator(mode = IdGenerationMode.INDIVIDUAL)
@EnableCaching
@SpringBootApplication
public class LolsearcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(LolsearcherApplication.class, args);
	}

}
