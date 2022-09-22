package com.lolsearcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class LolsearcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(LolsearcherApplication.class, args);
	}

}
