package com.lolsearcher.search.summoner;

public class SummonerConstant {

    public static final int ACCOUNT_ID_MIN_LENGTH = 1;
    public static final int ACCOUNT_ID_MAX_LENGTH = 56;
    public static final int PUUID_MIN_LENGTH = 1;
    public static final int PUUID_MAX_LENGTH = 78;
    public static final int SUMMONER_ID_MIN_LENGTH = 1;
    public static final int SUMMONER_ID_MAX_LENGTH = 63;
    public static final int SUMMONER_NAME_MIN_LENGTH = 1;
    public static final int SUMMONER_NAME_MAX_LENGTH = 50;
    public static final String FIND_BY_NAME_URI = "/summoner/{name}";
    public static final String SUMMONER_UPDATE_URI = "/renew/summoners";
    public static final String SUMMONER_NAME_REGEX = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)


}
