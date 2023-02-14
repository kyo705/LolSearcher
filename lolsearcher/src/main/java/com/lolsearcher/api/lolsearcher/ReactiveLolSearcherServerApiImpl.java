package com.lolsearcher.api.lolsearcher;

import com.lolsearcher.constant.UriConstants;
import com.lolsearcher.model.entity.summoner.Summoner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ReactiveLolSearcherServerApiImpl implements ReactiveLolSearcherServerApi {

    @Value("${lolsearcher.id}")
    private String LOLSEARCHER_SERVER_ID;
    @Value("${lolsearcher.password}")
    private String LOLSEARCHER_SERVER_PASSWORD;

    private final WebClient webClient;

    @Override
    public Summoner updateSameNameSummoners(List<String> summonerIds) {

        return webClient
                .put()
                .uri(UriConstants.REACTIVE_LOLSEARCHER_SERVER_SUMMONER_UPDATE_URI)
                .header(HttpHeaders.AUTHORIZATION, LOLSEARCHER_SERVER_ID, LOLSEARCHER_SERVER_PASSWORD) //토큰을 발급받아서 요청 권한을 갖도록 설계
                .body(BodyInserters.fromValue(summonerIds)) //summoners를 body에 넣어줌
                .retrieve()
                .bodyToMono(Summoner.class)
                .block();
    }
}
