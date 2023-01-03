package com.lolsearcher.constant;

public class LolSearcherConstants {


    public static final int MAX_SUMMONER_NAME_LENGTH = 50;
    public static final String REGEX = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)
    public static final long SUMMONER_RENEW_MS = 5*60*1000; //5min

    public static final int MATCH_DEFAULT_COUNT = 20;
    public static final int MATCH_ID_DEFAULT_COUNT = 100;
    public static final int MOST_CHAMP_LIMITED_COUNT = 5;
    public static final int LOGIN_BAN_COUNT = 10;
    public static final int SEARCH_BAN_COUNT = 20;

    public static final int CURRENT_SEASON_ID = 22;
    public static final String SOLO_RANK = "RANKED_SOLO_5x5";
    public static final String FLEX_RANK = "RANKED_FLEX_SR";

    public static final String TOP = "TOP";
    public static final String JUNGLE = "JUNGLE";
    public static final String MIDDLE = "MIDDLE";
    public static final String BOTTOM = "BOTTOM";
    public static final String UTILITY = "UTILITY";

    public static final String ALL = "all";
}
