package com.lolsearcher.model.factory;

import com.lolsearcher.model.entity.match.*;
import com.lolsearcher.model.entity.mostchamp.MostChampStat;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.request.riot.ingame.RiotGamesInGameBannedChampionDto;
import com.lolsearcher.model.request.riot.ingame.RiotGamesInGameDto;
import com.lolsearcher.model.request.riot.ingame.RiotGamesInGameParticipantDto;
import com.lolsearcher.model.request.riot.ingame.RiotGamesInGamePerksDto;
import com.lolsearcher.model.response.front.ingame.InGameBannedChampionDto;
import com.lolsearcher.model.response.front.ingame.InGameDto;
import com.lolsearcher.model.response.front.ingame.InGameParticipantDto;
import com.lolsearcher.model.response.front.ingame.InGamePerksDto;
import com.lolsearcher.model.response.front.match.*;
import com.lolsearcher.model.response.front.mostchamp.ResponseMostChampDto;
import com.lolsearcher.model.response.front.rank.RankDto;
import com.lolsearcher.model.response.front.summoner.SummonerDto;

public class FrontServerResponseDtoFactory {

    public static SummonerDto getSummonerDto(Summoner summoner, boolean renewed) {

        return SummonerDto
                .builder()
                .summonerId(summoner.getSummonerId())
                .puuId(summoner.getPuuid())
                .renewed(renewed)
                .name(summoner.getSummonerName())
                .profileIconId(summoner.getProfileIconId())
                .summonerLevel(summoner.getSummonerLevel())
                .lastRenewTimeStamp(summoner.getLastRenewTimeStamp())
                .build();
    }

    public static RankDto getRankDto(Rank rank) {

        return RankDto.builder()
                .summonerId(rank.getSummonerId())
                .seasonId(rank.getSeasonId())
                .queueType(rank.getQueueType())
                .tier(rank.getTier())
                .rank(rank.getRank())
                .leaguePoints(rank.getLeaguePoints())
                .leagueId(rank.getLeagueId())
                .wins(rank.getWins())
                .losses(rank.getLosses())
                .build();
    }

    public static MatchDto getResponseMatchDto(Match successMatch) {

        MatchDto matchDto = new MatchDto();
        matchDto.setQueueId(successMatch.getQueueId());
        matchDto.setSeasonId(successMatch.getSeasonId());
        matchDto.setVersion(successMatch.getVersion());
        matchDto.setGameDuration(successMatch.getGameDuration());
        matchDto.setGameEndTimestamp(successMatch.getGameEndTimestamp());

        if(successMatch.getTeams() == null){
            return matchDto;
        }
        for(Team team : successMatch.getTeams()){

            TeamDto teamDto = new TeamDto();
            teamDto.setTeamId(team.getTeamPositionId());
            teamDto.setGameResult(team.getGameResult());
            matchDto.getTeams().add(teamDto);

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

    public static InGameDto getResponseInGameDto(RiotGamesInGameDto riotGamesInGameDto){

        if(riotGamesInGameDto == null){
            return null;
        }
        InGameDto inGameDto = new InGameDto();
        inGameDto.setGameId(riotGamesInGameDto.getGameId());
        inGameDto.setGameType(riotGamesInGameDto.getGameType());
        inGameDto.setGameStartTime(riotGamesInGameDto.getGameStartTime());
        inGameDto.setMapId(riotGamesInGameDto.getMapId());
        inGameDto.setGameLength(riotGamesInGameDto.getGameLength());
        inGameDto.setPlatformId(riotGamesInGameDto.getPlatformId());
        inGameDto.setGameMode(riotGamesInGameDto.getGameMode());
        inGameDto.setGameQueueConfigId(riotGamesInGameDto.getGameQueueConfigId());

        if(riotGamesInGameDto.getBannedChampions() != null){
            for(RiotGamesInGameBannedChampionDto bannedChampionInfo : riotGamesInGameDto.getBannedChampions()){

                InGameBannedChampionDto inGameBannedChampionDto = InGameBannedChampionDto.builder()
                        .championId(bannedChampionInfo.getChampionId())
                        .pickTurn(bannedChampionInfo.getPickTurn())
                        .teamId(bannedChampionInfo.getTeamId())
                        .build();

                inGameDto.getBannedChampions().add(inGameBannedChampionDto);
            }
        }

        if(riotGamesInGameDto.getParticipants() != null){
            for(RiotGamesInGameParticipantDto riotGamesInGameParticipantDto : riotGamesInGameDto.getParticipants()){

                InGameParticipantDto inGameParticipantDto = InGameParticipantDto.builder()
                        .championId(riotGamesInGameParticipantDto.getChampionId())
                        .profileIconId(riotGamesInGameParticipantDto.getProfileIconId())
                        .bot(riotGamesInGameParticipantDto.isBot())
                        .teamId(riotGamesInGameParticipantDto.getTeamId())
                        .summonerName(riotGamesInGameParticipantDto.getSummonerName())
                        .summonerId(riotGamesInGameParticipantDto.getSummonerId())
                        .spell1Id(riotGamesInGameParticipantDto.getSpell1Id())
                        .spell2Id(riotGamesInGameParticipantDto.getSpell2Id())
                        .build();

                inGameDto.getParticipants().add(inGameParticipantDto);

                RiotGamesInGamePerksDto riotGamesInGamePerksDto = riotGamesInGameParticipantDto.getPerks();
                if(riotGamesInGamePerksDto != null){
                    InGamePerksDto inGamePerksDto =
                            InGamePerksDto.builder()
                                    .perkStyle(riotGamesInGamePerksDto.getPerkStyle())
                                    .perkSubStyle(riotGamesInGamePerksDto.getPerkSubStyle())
                                    .mainPerk1(riotGamesInGamePerksDto.getPerkIds().get(0))
                                    .mainPerk2(riotGamesInGamePerksDto.getPerkIds().get(1))
                                    .mainPerk3(riotGamesInGamePerksDto.getPerkIds().get(2))
                                    .mainPerk4(riotGamesInGamePerksDto.getPerkIds().get(3))
                                    .subPerk1(riotGamesInGamePerksDto.getPerkIds().get(4))
                                    .subPerk2(riotGamesInGamePerksDto.getPerkIds().get(5))
                                    .statPerk1(riotGamesInGamePerksDto.getPerkIds().get(6))
                                    .statPerk2(riotGamesInGamePerksDto.getPerkIds().get(7))
                                    .statPerk3(riotGamesInGamePerksDto.getPerkIds().get(8))
                                    .build();

                    inGameParticipantDto.setPerks(inGamePerksDto);
                }
            }
        }
        return inGameDto;
    }
}
