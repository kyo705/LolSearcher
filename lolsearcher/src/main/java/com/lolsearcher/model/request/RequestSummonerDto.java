package com.lolsearcher.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.lolsearcher.constant.LolSearcherConstants.MAX_SUMMONER_NAME_LENGTH;

@Builder
@AllArgsConstructor
@Data
public class RequestSummonerDto {

    @Size(max = MAX_SUMMONER_NAME_LENGTH)
    @NotNull
    private String summonerName;

    public RequestSummonerDto(){
        summonerName = "";
    }
}
