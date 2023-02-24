package com.lolsearcher.unit.controller.summoner;

import com.lolsearcher.controller.search.summoner.SummonerController;
import com.lolsearcher.model.request.search.summoner.RequestSummonerDto;
import com.lolsearcher.model.response.front.search.summoner.SummonerDto;
import com.lolsearcher.service.search.summoner.SummonerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.lolsearcher.constant.LolSearcherConstants.REGEX;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SummonerControllerUnitTest {

    @Mock
    private SummonerService summonerService;
    private SummonerController summonerController;

    @BeforeEach
    public void setup(){
        summonerController = new SummonerController(summonerService);
    }

    @DisplayName("정상적인 파라미터가 주어진 경우 DTO를 반환한다.")
    @Test
    public void getSummonerTestWithValidParam(){

        //given
        RequestSummonerDto requestBody = new RequestSummonerDto("유저1");
        SummonerDto summonerDto = SummonerDto
                .builder()
                .summonerId("summonerId1")
                .name(requestBody.getSummonerName())
                .build();

        BDDMockito.given(summonerService.getSummonerDto(requestBody)).willReturn(summonerDto);

        //when
        SummonerDto answer = summonerController.getSummoner(requestBody);

        //then
        assertThat(answer.getName()).isEqualTo(requestBody.getSummonerName());
    }

    @DisplayName("특수문자가 있는 닉네임이 주어진 경우 필터된 닉네임으로 DTO를 반환한다.")
    @Test
    public void getSummonerTestWithInvalidParam(){

        //given
        RequestSummonerDto requestBody = new RequestSummonerDto("유#!저1");
        String unfilteredName = requestBody.getSummonerName();

        SummonerDto summonerDto = SummonerDto
                .builder()
                .summonerId("summonerId1")
                .name(unfilteredName.replaceAll(REGEX, ""))
                .build();

        BDDMockito.given(summonerService.getSummonerDto(requestBody)).willReturn(summonerDto);

        //when
        SummonerDto answer = summonerController.getSummoner(requestBody);

        //then
        assertThat(answer.getName()).isNotEqualTo(unfilteredName);
        assertThat(answer.getName()).isEqualTo(unfilteredName.replaceAll(REGEX, ""));

        assertThat(requestBody.getSummonerName()).isNotEqualTo(unfilteredName);
        assertThat(requestBody.getSummonerName()).isEqualTo(unfilteredName.replaceAll(REGEX, ""));
    }
}
