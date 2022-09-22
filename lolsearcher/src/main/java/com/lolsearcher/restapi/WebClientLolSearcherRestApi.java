package com.lolsearcher.restapi;

import java.util.Map;
import java.util.function.Consumer;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import com.lolsearcher.domain.Dto.summoner.SummonerDto;

@Component
public class WebClientLolSearcherRestApi implements LolsearcherRestApi {

	private WebClient webClient;
	private static final String url = "http://localhost:8080";
	
	public WebClientLolSearcherRestApi(WebClient webClient) {
		this.webClient = webClient;
	}
	
	@Override
	public ResponseEntity<Map> getSummonerById(String summonerid, String sessionid) {
		
		/*.uri(UriBuilder -> UriBuilder.path("http://localhost:8080/login")
				.queryParam("username", username)
				.queryParam("password", password)
				.build())*/
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("username", "admin");
		params.add("password", "135");
		
		/*String jsessionId = webClient
				.post()
				.uri("http://localhost:8080/login")
				.body(BodyInserters.fromFormData(params))
				.retrieve().toEntity(String.class)
				.block()
				.getHeaders()
				.get("Set-Cookie").get(0);
		
		System.out.println(
				 );*/
			
		
		System.out.println(webClient.get().uri(
				url+"/api/summoner/name/"+summonerid).cookie("JSESSIONID", sessionid));
				
		
		
		ResponseEntity<Map> summoner = webClient.get().uri(
				url+"/api/summoner/name/"+summonerid).cookie("JSESSIONID", sessionid)
				.retrieve().toEntity(Map.class).block();
		
		
		return summoner;
	}

}
