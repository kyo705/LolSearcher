package com.lolsearcher.utils;

import com.lolsearcher.login.LolsearcherUserDetails;
import com.lolsearcher.search.champion.dto.ChampEnemyStatsDto;
import com.lolsearcher.search.champion.dto.ChampItemStatsDto;
import com.lolsearcher.search.champion.dto.ChampPositionStatsDto;
import com.lolsearcher.search.champion.entity.ChampEnemyStats;
import com.lolsearcher.search.champion.entity.ChampItemStats;
import com.lolsearcher.search.champion.entity.ChampPositionStats;
import com.lolsearcher.search.match.dto.DetailMemberDto;
import com.lolsearcher.search.match.dto.MatchDto;
import com.lolsearcher.search.match.dto.PerksDto;
import com.lolsearcher.search.match.dto.SummaryMemberDto;
import com.lolsearcher.search.match.entity.DetailMember;
import com.lolsearcher.search.match.entity.Match;
import com.lolsearcher.search.match.entity.Perks;
import com.lolsearcher.search.match.entity.SummaryMember;
import com.lolsearcher.search.mostchamp.MostChamp;
import com.lolsearcher.search.mostchamp.MostChampDto;
import com.lolsearcher.search.rank.Rank;
import com.lolsearcher.search.rank.RankDto;
import com.lolsearcher.search.summoner.Summoner;
import com.lolsearcher.search.summoner.SummonerDto;
import com.lolsearcher.user.Role;
import com.lolsearcher.user.User;
import com.lolsearcher.user.UserDto;

public class ResponseDtoFactory {

    public static SummonerDto getSummonerDto(Summoner summoner) {

        return SummonerDto
                .builder()
                .summonerId(summoner.getSummonerId())
                .puuId(summoner.getPuuid())
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
                .leagueId(rank.getLeagueId().orElse(""))
                .wins(rank.getWins())
                .losses(rank.getLosses())
                .build();
    }

    public static MatchDto getResponseMatchDto(Match match) {

        MatchDto matchDto = new MatchDto();
        matchDto.setMatchId(match.getMatchId());
        matchDto.setQueueId(match.getQueueId());
        matchDto.setSeasonId(match.getSeasonId());
        matchDto.setVersion(match.getVersion());
        matchDto.setGameDuration(match.getGameDuration());
        matchDto.setGameEndTimestamp(match.getGameEndTimestamp());

        for(SummaryMember summaryMember : match.getMembers()) {

            SummaryMemberDto summaryMemberDto = new SummaryMemberDto();
            matchDto.getSummaryMember().add(summaryMemberDto);

            summaryMemberDto.setSummonerId(summaryMember.getSummonerId());
            summaryMemberDto.setBanChampionId(summaryMember.getBanChampionId());
            summaryMemberDto.setPickChampionId(summaryMember.getPickChampionId());
            summaryMemberDto.setPositionId(summaryMember.getPositionId());
            summaryMemberDto.setChampionLevel(summaryMember.getChampionLevel());
            summaryMemberDto.setMinionKills(summaryMember.getMinionKills());
            summaryMemberDto.setKills(summaryMember.getKills());
            summaryMemberDto.setDeaths(summaryMember.getDeaths());
            summaryMemberDto.setAssists(summaryMember.getAssists());
            summaryMemberDto.setItem0(summaryMember.getItem0());
            summaryMemberDto.setItem1(summaryMember.getItem1());
            summaryMemberDto.setItem2(summaryMember.getItem2());
            summaryMemberDto.setItem3(summaryMember.getItem3());
            summaryMemberDto.setItem4(summaryMember.getItem4());
            summaryMemberDto.setItem5(summaryMember.getItem5());
            summaryMemberDto.setItem6(summaryMember.getItem6());

            DetailMember detailMember = summaryMember.getDetailMember();
            DetailMemberDto detailMemberDto = new DetailMemberDto();
            summaryMemberDto.setDetailDto(detailMemberDto);

            detailMemberDto.setGoldEarned(detailMember.getGoldEarned());
            detailMemberDto.setGoldSpent(detailMember.getGoldSpent());
            detailMemberDto.setTotalDamageDealt(detailMember.getTotalDamageDealt());
            detailMemberDto.setTotalDamageDealtToChampions(detailMember.getTotalDamageDealtToChampions());
            detailMemberDto.setTotalDamageShieldedOnTeammates(detailMember.getTotalDamageShieldedOnTeammates());
            detailMemberDto.setTotalDamageTaken(detailMember.getTotalDamageTaken());
            detailMemberDto.setTimeCCingOthers(detailMember.getTimeCCingOthers());
            detailMemberDto.setTotalHeal(detailMember.getTotalHeal());
            detailMemberDto.setTotalHealsOnTeammates(detailMember.getTotalHealsOnTeammates());
            detailMemberDto.setDetectorWardPurchased(detailMember.getDetectorWardPurchased());
            detailMemberDto.setDetectorWardsPlaced(detailMember.getDetectorWardsPlaced());
            detailMemberDto.setWardKills(detailMember.getWardKills());
            detailMemberDto.setWardsPlaced(detailMember.getWardsPlaced());

            Perks perks = summaryMember.getPerks();
            PerksDto perksDto = new PerksDto();
            summaryMemberDto.setPerksDto(perksDto);

            perksDto.setMainPerkStyle(perks.getMainPerkStyle());
            perksDto.setSubPerkStyle(perks.getSubPerkStyle());
            perksDto.setMainPerk1(perks.getMainPerk1());
            perksDto.setMainPerk2(perks.getMainPerk2());
            perksDto.setMainPerk3(perks.getMainPerk3());
            perksDto.setMainPerk4(perks.getMainPerk4());
            perksDto.setSubPerk1(perks.getSubPerk1());
            perksDto.setSubPerk2(perks.getSubPerk2());
            perksDto.setOffense(perks.getOffense());
            perksDto.setFlex(perks.getFlex());
            perksDto.setDefense(perks.getDefense());
        }

        return  matchDto;
    }

    public static MostChampDto getResponseMostChampDto(MostChamp mostChamp) {

        return MostChampDto.builder()
                .summonerId(mostChamp.getSummonerId())
                .seasonId(mostChamp.getSeasonId())
                .championId(mostChamp.getChampionId())
                .queueId(mostChamp.getQueueId())
                .totalGames(mostChamp.getTotalGames())
                .totalWins(mostChamp.getTotalWins())
                .totalLosses(mostChamp.getTotalLosses())
                .totalKills(mostChamp.getTotalKills())
                .totalAssists(mostChamp.getTotalAssists())
                .totalDeaths(mostChamp.getTotalDeaths())
                .totalMinionKills(mostChamp.getTotalMinionKills())
                .build();
    }

    public static ChampPositionStatsDto getChampPositionStatsDto(ChampPositionStats champPositionStats) {

        return ChampPositionStatsDto.builder()
                .gameVersion(champPositionStats.getGameVersion())
                .championId(champPositionStats.getChampionId())
                .positionId(champPositionStats.getPositionId())
                .wins(champPositionStats.getWins())
                .losses(champPositionStats.getLosses())
                .bans(champPositionStats.getBans())
                .build();
    }

    public static ChampItemStatsDto getChampItemStatsDto(ChampItemStats champItemStats) {

        return ChampItemStatsDto.builder()
                .gameVersion(champItemStats.getGameVersion())
                .championId(champItemStats.getChampionId())
                .itemId(champItemStats.getItemId())
                .wins(champItemStats.getWins())
                .losses(champItemStats.getLosses())
                .build();
    }

    public static ChampEnemyStatsDto getChampEnemyStatsDto(ChampEnemyStats champEnemyStats) {

        return ChampEnemyStatsDto.builder()
                .gameVersion(champEnemyStats.getGameVersion())
                .championId(champEnemyStats.getChampionId())
                .enemyChampionId(champEnemyStats.getEnemyChampionId())
                .wins(champEnemyStats.getWins())
                .losses(champEnemyStats.getLosses())
                .build();
    }

    public static UserDto getUserDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getUsername())
                .role(user.getRole())
                .loginSecurity(user.getLoginSecurity())
                .lastLoginTimestamp(user.getLastLoginTimeStamp())
                .build();
    }

    public static UserDto getUserDto(LolsearcherUserDetails user) {

        return UserDto.builder()
                .id(user.getId())
                .email(user.getUsername())
                .name(user.getNickname())
                .role(Role.of(user.getAuthorities().stream().findAny().orElseThrow().getAuthority()))
                .loginSecurity(user.getLoginSecurity())
                .lastLoginTimestamp(user.getLastLoginTimestamp())
                .build();
    }
}
