package com.lolsearcher.constant;

public class UriConstants {
    public static final String LOGIN_URI = "/login";
    public static final String NON_AUTHORITY_URI = "/rejected";
    public static final String LOGIN_FORM_URI = "/loginForm";
    public static final String LOLSEARCHER_FRONT_SERVER_URI = "localhost";
    public static final String RIOTGAMES_SUMMONER_WITH_ID_URI = "/lol/summoner/v4/summoners/{id}?api_key={key}";
    public static final String RIOTGAMES_SUMMONER_WITH_NAME_URI = "/lol/summoner/v4/summoners/by-name/{name}?api_key={key}";
    public static final String RIOTGAMES_RANK_WITH_ID_URI = "/lol/league/v4/entries/by-summoner/{id}?api_key={key}";
    public static final String RIOTGAMES_MATCHIDS_WITH_PUUID_URI = "/lol/match/v5/matches/by-puuid/{puuid}/ids?start={start}&count={count}&api_key={key}";
    public static final String RIOTGAMES_MATCH_WITH_ID_URI = "/lol/match/v5/matches/{matchId}?api_key={key}";
    public static final String RIOTGAMES_INGAME_WITH_ID_URI = "/lol/spectator/v4/active-games/by-summoner/{id}?api_key={key}";

}
