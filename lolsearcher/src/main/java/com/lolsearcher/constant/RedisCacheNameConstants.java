package com.lolsearcher.constant;


public class RedisCacheNameConstants {

    public static final String CACHE_PREFIX = "cache:";
    public static final String CHAMPION_ID_LIST = CACHE_PREFIX + "champions";
    public static final String LOGIN_BAN = CACHE_PREFIX + "loginBan";
    public static final String SEARCH_BAN = CACHE_PREFIX + "searchBan";
    public static final String SUCCEEDED_IDENTIFICATION_TOKEN = CACHE_PREFIX + "succeededIdentificationToken";
}
