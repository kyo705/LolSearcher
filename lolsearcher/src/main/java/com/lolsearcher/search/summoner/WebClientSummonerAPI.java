package com.lolsearcher.search.summoner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_UPDATE_URI;

@RequiredArgsConstructor
@Component
public class WebClientSummonerAPI implements SummonerAPI {

    private final WebClient reactiveLolSearcherWebClient;

    @Override
    public Optional<Summoner> updateSameNameSummoners(List<String> summonerIds) {

        Summoner summoner = reactiveLolSearcherWebClient
                .put()
                .uri(SUMMONER_UPDATE_URI)
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .body(BodyInserters.fromValue(summonerIds))
                .retrieve()
                .bodyToMono(Summoner.class)
                .block();

        if(summoner == null) {
            return Optional.empty();
        }
        summoner.validate();
        return Optional.of(summoner);
    }
}
