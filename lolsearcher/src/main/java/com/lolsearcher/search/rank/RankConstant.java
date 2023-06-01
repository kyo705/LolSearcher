package com.lolsearcher.search.rank;

public class RankConstant {

    public static final int LEAGUE_ID_MIN_LENGTH = 1;
    public static final int LEAGUE_ID_MAX_LENGTH = 50;
    public static final int MAX_COUNT_PER_RANK_TYPE = 1;
    public static final int THE_NUMBER_OF_RANK_TYPE = 2; /* 랭크 게임 종류 수 */
    public static final int CURRENT_SEASON_ID = 23;
    public static final int  INITIAL_SEASON_ID = 10;
    public static final String DEFAULT_SEASON_ID = "23";
    public static final String FIND_RANKS_URI = "/summoner/{summonerId}/ranks";
    public static final String FIND_RANK_BY_ID_URI = "/summoner/{summonerId}/rank/{rankId}";
}
