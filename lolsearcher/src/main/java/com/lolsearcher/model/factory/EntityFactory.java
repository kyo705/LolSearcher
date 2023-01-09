package com.lolsearcher.model.factory;

import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.model.entity.match.*;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.request.riot.match.RiotGamesMatchDto;
import com.lolsearcher.model.request.riot.match.RiotGamesParticipantDto;
import com.lolsearcher.model.request.riot.match.RiotGamesTotalMatchDto;
import com.lolsearcher.model.request.riot.match.perk.RiotGamesMatchPerkStatsDto;
import com.lolsearcher.model.request.riot.match.perk.RiotGamesMatchPerkStyleDto;
import com.lolsearcher.model.request.riot.match.team.RiotGamesTeamDto;
import com.lolsearcher.model.request.riot.rank.RiotGamesRankDto;
import com.lolsearcher.model.request.riot.summoner.RiotGamesSummonerDto;

import java.util.List;

public class EntityFactory {

    public static Summoner getSummonerFromRestApiDto(RiotGamesSummonerDto riotGamesSummonerDto){

        return Summoner.builder()
                .accountId(riotGamesSummonerDto.getAccountId())
                .summonerId(riotGamesSummonerDto.getId())
                .puuid(riotGamesSummonerDto.getPuuid())
                .summonerName(riotGamesSummonerDto.getName())
                .profileIconId(riotGamesSummonerDto.getProfileIconId())
                .revisionDate(riotGamesSummonerDto.getRevisionDate())
                .summonerLevel(riotGamesSummonerDto.getSummonerLevel())
                .lastMatchId("")
                .lastRenewTimeStamp(System.currentTimeMillis())
                .build();
    }

    public static Rank getRankFromRestApiDto(RiotGamesRankDto riotGamesRankDto){

        return Rank.builder()
                .summonerId(riotGamesRankDto.getSummonerId())
                .queueType(riotGamesRankDto.getQueueType())
                .leagueId(riotGamesRankDto.getLeagueId())
                .tier(riotGamesRankDto.getTier())
                .rank(riotGamesRankDto.getRank())
                .leaguePoints(riotGamesRankDto.getLeaguePoints())
                .wins(riotGamesRankDto.getWins())
                .losses(riotGamesRankDto.getLosses())
                .seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
                .build();
    }

    public static Match getMatchFromRestApiDto(RiotGamesTotalMatchDto riotGamesTotalMatchDto) throws IllegalAccessException {

        RiotGamesMatchDto riotGamesMatchDto = riotGamesTotalMatchDto.getInfo();

        Match match = new Match();
        match.setMatchId(riotGamesTotalMatchDto.getMetadata().getMatchId());
        match.setGameDuration(riotGamesMatchDto.getGameDuration());
        match.setGameEndTimestamp(riotGamesMatchDto.getGameEndTimestamp());
        match.setQueueId(riotGamesMatchDto.getQueueId());
        match.setSeasonId(LolSearcherConstants.CURRENT_SEASON_ID);
        match.setVersion(riotGamesMatchDto.getGameVersion());

        for(RiotGamesTeamDto riotGamesTeamDto : riotGamesMatchDto.getTeams()){

            Team team = new Team();
            team.setTeamPositionId(riotGamesTeamDto.getTeamId());
            team.setGameResult(riotGamesMatchDto.getGameDuration() > 1000*60*10 ? (riotGamesTeamDto.isWin() ? (byte) 0 : 1) : 2);
            team.setMatch(match);

            for(RiotGamesParticipantDto participantDto : riotGamesMatchDto.getParticipants()){

                SummaryMember summaryMember = new SummaryMember();
                summaryMember.setSummonerId(participantDto.getSummonerId());
                summaryMember.setPickChampionId(participantDto.getChampionId());
                summaryMember.setChampionLevel(participantDto.getChampLevel());
                summaryMember.setMinionKills((short) (participantDto.getNeutralMinionsKilled() + participantDto.getTotalMinionsKilled()));
                summaryMember.setKills(participantDto.getKills());
                summaryMember.setDeaths(participantDto.getDeaths());
                summaryMember.setAssists(participantDto.getAssists());
                summaryMember.setItem0(participantDto.getItem0());
                summaryMember.setItem1(participantDto.getItem1());
                summaryMember.setItem2(participantDto.getItem2());
                summaryMember.setItem3(participantDto.getItem3());
                summaryMember.setItem4(participantDto.getItem4());
                summaryMember.setItem5(participantDto.getItem5());
                summaryMember.setItem6(participantDto.getItem6());
                summaryMember.setTeam(team);

                List<RiotGamesMatchPerkStyleDto> perkStyleDtos = participantDto.getPerks().getStyles();
                Perks perks = new Perks();
                perks.setMainPerkStyle(perkStyleDtos.get(0).getStyle());
                perks.setSubPerkStyle(perkStyleDtos.get(1).getStyle());
                perks.setMainPerk1(perkStyleDtos.get(0).getSelections().get(0).getPerk());
                perks.setMainPerk1Var1(perkStyleDtos.get(0).getSelections().get(0).getVar1());
                perks.setMainPerk1Var2(perkStyleDtos.get(0).getSelections().get(0).getVar2());
                perks.setMainPerk1Var3(perkStyleDtos.get(0).getSelections().get(0).getVar3());
                perks.setMainPerk2(perkStyleDtos.get(0).getSelections().get(1).getVar1());
                perks.setMainPerk2Var1(perkStyleDtos.get(0).getSelections().get(1).getVar1());
                perks.setMainPerk2Var2(perkStyleDtos.get(0).getSelections().get(1).getVar2());
                perks.setMainPerk2Var3(perkStyleDtos.get(0).getSelections().get(1).getVar3());
                perks.setMainPerk3(perkStyleDtos.get(0).getSelections().get(2).getVar1());
                perks.setMainPerk3Var1(perkStyleDtos.get(0).getSelections().get(2).getVar1());
                perks.setMainPerk3Var2(perkStyleDtos.get(0).getSelections().get(2).getVar2());
                perks.setMainPerk3Var3(perkStyleDtos.get(0).getSelections().get(2).getVar3());
                perks.setMainPerk4(perkStyleDtos.get(0).getSelections().get(3).getVar1());
                perks.setMainPerk4Var1(perkStyleDtos.get(0).getSelections().get(3).getVar1());
                perks.setMainPerk4Var2(perkStyleDtos.get(0).getSelections().get(3).getVar2());
                perks.setMainPerk4Var3(perkStyleDtos.get(0).getSelections().get(3).getVar3());
                perks.setSubPerk1(perkStyleDtos.get(1).getSelections().get(0).getVar1());
                perks.setSubPerk1Var1(perkStyleDtos.get(1).getSelections().get(0).getVar1());
                perks.setSubPerk1Var2(perkStyleDtos.get(1).getSelections().get(0).getVar2());
                perks.setSubPerk1Var3(perkStyleDtos.get(1).getSelections().get(0).getVar3());
                perks.setSubPerk2(perkStyleDtos.get(1).getSelections().get(1).getVar1());
                perks.setSubPerk2Var1(perkStyleDtos.get(1).getSelections().get(1).getVar1());
                perks.setSubPerk2Var2(perkStyleDtos.get(1).getSelections().get(1).getVar2());
                perks.setSubPerk2Var3(perkStyleDtos.get(1).getSelections().get(1).getVar3());
                perks.setSummaryMember(summaryMember);

                RiotGamesMatchPerkStatsDto perkStatsDto = participantDto.getPerks().getStatPerks();
                PerkStats perkStats = new PerkStats();
                perkStats.setDefense(perkStatsDto.getDefense());
                perkStats.setOffense(perkStatsDto.getOffense());
                perkStats.setFlex(perkStatsDto.getFlex());
                perks.setPerkStats(perkStats);

                DetailMember detailMember = new DetailMember();
                detailMember.setGoldEarned(participantDto.getGoldEarned());
                detailMember.setGoldSpent(participantDto.getGoldSpent());
                detailMember.setTotalDamageDealt(participantDto.getTotalDamageDealt());
                detailMember.setTotalDamageDealtToChampions(participantDto.getTotalDamageDealtToChampions());
                detailMember.setTotalDamageShieldedOnTeammates(participantDto.getTotalDamageShieldedOnTeammates());
                detailMember.setTotalDamageTaken(participantDto.getTotalDamageTaken());
                detailMember.setTimeCCingOthers(participantDto.getTimeCCingOthers());
                detailMember.setTotalHeal(participantDto.getTotalHeal());
                detailMember.setTotalHealsOnTeammates(participantDto.getTotalHealsOnTeammates());
                detailMember.setDetectorWardPurchased(participantDto.getVisionWardsBoughtInGame());
                detailMember.setDetectorWardsPlaced(participantDto.getWardsPlaced());
                detailMember.setWardKills(participantDto.getWardsKilled());
                detailMember.setWardsPlaced(participantDto.getWardsPlaced());
                detailMember.setSummaryMember(summaryMember);
            }
        }
        return match;
    }
}
