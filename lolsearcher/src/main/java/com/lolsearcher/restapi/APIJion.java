package com.lolsearcher.restapi;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class APIJion<T>{
	String url;
	String key="RGAPI-2a0ac3ef-7f65-4854-97d4-54e2c7b3dbab";
	public APIJion(String url) {
		this.url=url;
	}
	public T Apijion() throws Exception{
		RestTemplate restTemplate = new RestTemplate(); 
        HttpHeaders header = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(header);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url+"api_key="+key).build();
		
		return restTemplate.exchange(uri.toString(), HttpMethod.GET, entity,
				new ParameterizedTypeReference<T>(){}).getBody();
	}
}