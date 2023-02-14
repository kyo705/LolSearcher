package com.lolsearcher.model.factory;

import com.lolsearcher.model.entity.match.*;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.response.front.match.*;
import com.lolsearcher.model.response.openapi.OpenApiRankDto;
import com.lolsearcher.model.response.openapi.OpenApiSummonerDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class OpenApiResponseDtoFactory {

    public static <T> ResponseEntity<T> getResponseEntity(HttpHeaders headers, T body){

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(body);
    }

    public static OpenApiSummonerDto getOpenApiSummonerDtoFromEntity(Summoner summoner) {

        return OpenApiSummonerDto.builder()
                .summonerId(summoner.getSummonerId())
                .puuId(summoner.getPuuid())
                .name(summoner.getSummonerName())
                .profileIconId(summoner.getProfileIconId())
                .summonerLevel(summoner.getSummonerLevel())
                .build();
    }

    public static OpenApiRankDto getOpenApiRankDtoFromEntity(Rank rank){

        return OpenApiRankDto.builder()
                .summonerId(rank.getSummonerId())
                .seasonId(rank.getSeasonId())
                .queueType(rank.getQueueType())
                .leagueId(rank.getLeagueId())
                .tier(rank.getTier())
                .rank(rank.getRank())
                .leaguePoints(rank.getLeaguePoints())
                .wins(rank.getWins())
                .losses(rank.getLosses())
                .build();
    }

    public static MatchDto getOpenApiMatchDtoFromEntity(Match match) {

        MatchDto matchDto = new MatchDto();
        matchDto.setQueueId(match.getQueueId());
        matchDto.setSeasonId(match.getSeasonId());
        matchDto.setVersion(match.getVersion());
        matchDto.setGameDuration(match.getGameDuration());
        matchDto.setGameEndTimestamp(match.getGameEndTimestamp());

        if(match.getTeams() == null){
            return matchDto;
        }
        for(Team team : match.getTeams()){

            TeamDto teamDto = new TeamDto();
            teamDto.setTeamId(team.getTeamPositionId());
            teamDto.setGameResult(team.getGameResult());

            if(team.getMembers() == null){
                return matchDto;
            }
            for(SummaryMember summaryMember : team.getMembers()){

                ParticipantDto participantDto = new ParticipantDto();
                participantDto.setSummonerId(summaryMember.getSummonerId());
                participantDto.setBanChampionId(summaryMember.getBanChampionId());
                participantDto.setPickChampionId(summaryMember.getPickChampionId());
                participantDto.setPositionId(summaryMember.getPositionId());
                participantDto.setChampionLevel(summaryMember.getChampionLevel());
                participantDto.setMinionKills(summaryMember.getMinionKills());

                participantDto.setKills(summaryMember.getKills());
                participantDto.setDeaths(summaryMember.getDeaths());
                participantDto.setAssists(summaryMember.getAssists());

                participantDto.setItem0(summaryMember.getItem0());
                participantDto.setItem1(summaryMember.getItem1());
                participantDto.setItem2(summaryMember.getItem2());
                participantDto.setItem3(summaryMember.getItem3());
                participantDto.setItem4(summaryMember.getItem4());
                participantDto.setItem5(summaryMember.getItem5());
                participantDto.setItem6(summaryMember.getItem6());

                teamDto.getBanList().add(summaryMember.getBanChampionId());
                teamDto.getParticipantDtoList().add(participantDto);

                DetailMember detailMember = summaryMember.getDetailMember();
                if(detailMember == null){
                    return matchDto;
                }
                ParticipantDetailDto detailDto = new ParticipantDetailDto();
                detailDto.setGoldEarned(detailMember.getGoldEarned());
                detailDto.setGoldSpent(detailMember.getGoldSpent());
                detailDto.setTotalDamageDealt(detailMember.getTotalDamageDealt());
                detailDto.setTotalDamageDealtToChampions(detailMember.getTotalDamageDealtToChampions());
                detailDto.setTotalDamageShieldedOnTeammates(detailMember.getTotalDamageShieldedOnTeammates());
                detailDto.setTotalDamageTaken(detailMember.getTotalDamageTaken());
                detailDto.setTimeCCingOthers(detailMember.getTimeCCingOthers());
                detailDto.setTotalHeal(detailMember.getTotalHeal());
                detailDto.setTotalHealsOnTeammates(detailMember.getTotalHealsOnTeammates());
                detailDto.setDetectorWardPurchased(detailMember.getDetectorWardPurchased());
                detailDto.setDetectorWardsPlaced(detailMember.getDetectorWardsPlaced());
                detailDto.setWardKills(detailMember.getWardKills());
                detailDto.setWardsPlaced(detailMember.getWardsPlaced());

                participantDto.setDetailDto(detailDto);

                Perks perks = summaryMember.getPerks();
                if(perks == null){
                    return matchDto;
                }
                PerksDto perksDto = new PerksDto();
                perksDto.setMainPerkStyle(perks.getMainPerkStyle());
                perksDto.setSubPerkStyle(perks.getSubPerkStyle());
                perksDto.setMainPerk1(perks.getMainPerk1());
                perksDto.setMainPerk2(perks.getMainPerk2());
                perksDto.setMainPerk3(perks.getMainPerk3());
                perksDto.setMainPerk4(perks.getMainPerk4());
                perksDto.setSubPerk1(perks.getSubPerk1());
                perksDto.setSubPerk2(perks.getSubPerk2());

                detailDto.setPerksDto(perksDto);
                participantDto.setMainPerk1(summaryMember.getPerks().getMainPerk1());
                participantDto.setSubPerkStyle(summaryMember.getPerks().getSubPerkStyle());

                PerkStats perkStats = summaryMember.getPerks().getPerkStats();
                if(perkStats == null){
                    return matchDto;
                }
                PerkStatsDto perkStatsDto = new PerkStatsDto();
                perkStatsDto.setDefense(perkStats.getDefense());
                perkStatsDto.setFlex(perkStats.getFlex());
                perkStatsDto.setOffense(perkStats.getOffense());

                detailDto.setPerkStatsDto(perkStatsDto);
            }
        }

        return  matchDto;
    }
}
