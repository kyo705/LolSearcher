package com.lolsearcher.constant;

public class UriConstants {
    public static final String LOGIN_URI = "/login";
    public static final String SECOND_LEVEL_LOGIN_FORM_URI = "/identification/login";
    public static final String JOIN_AUTHENTICATION_URI = "/identification/join";

    /* external server uri */
    public static final String NOTIFICATION_SERVER_IDENTIFICATION_URI = "/notification/identification";
    public static final String REACTIVE_LOLSEARCHER_SERVER_SUMMONER_UPDATE_URI = "/renew/summoners";
    public static final String LOLSEARCHER_FRONT_SERVER_URI = "localhost:80"; /* Nginx uri */
    public static final String SUMMONER_RENEW_REQUEST_URI = "localhost:80/renew/summoner";
}
