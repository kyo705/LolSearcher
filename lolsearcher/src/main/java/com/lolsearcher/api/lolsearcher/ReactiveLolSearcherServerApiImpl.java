package com.lolsearcher.api.lolsearcher;

import com.lolsearcher.model.entity.summoner.Summoner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.lolsearcher.constant.UriConstants.REACTIVE_LOLSEARCHER_SERVER_SUMMONER_UPDATE_URI;

@RequiredArgsConstructor
@Component
public class ReactiveLolSearcherServerApiImpl implements ReactiveLolSearcherServerApi {

    private final WebClient reactiveLolSearcherWebClient;

    @Override
    public Summoner updateSameNameSummoners(List<String> summonerIds) {

        return reactiveLolSearcherWebClient
                .put()
                .uri(REACTIVE_LOLSEARCHER_SERVER_SUMMONER_UPDATE_URI)
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON)) //토큰을 발급받아서 요청 권한을 갖도록 설계
                .body(BodyInserters.fromValue(summonerIds)) //summoners를 body에 넣어줌
                .retrieve()
                .bodyToMono(Summoner.class)
                .block();
    }
}
