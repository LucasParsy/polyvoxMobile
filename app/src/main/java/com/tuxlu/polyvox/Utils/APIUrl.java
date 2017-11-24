package com.tuxlu.polyvox.Utils;

/**
 * Created by tuxlu on 16/09/17.
 */

public class APIUrl {
    public static final String SCHEME = "https";
    public static final String AUTHORITY = "api.polyvox.fr";
    public static final String BASE_URL = SCHEME + "://" + AUTHORITY + "/";

    public static final String DISCOVER_ROOMS = "discoverRoom";
    public static final String SEARCH_ROOMS = "searchRoom";

    public static final String CREATE_ACCOUNT = "createAccount";
    public static final String INFO_ROOM = "infoRoom";
    public static final String INFO_USER = "infoUser";
    public static final String VIDEO_STREAM = "video";
    public static final String COOKIE_HEADER = "Cookie";
    public static final String INVALID_CREDENTIALS_JSON = "invalidCredentials";
    //public static final String GET_CHAT = "getChat";
    //public static final String SEND_CHAT = "sendChat";

    public static final String LOGIN = "login";
    public static final String LOGIN_PARAM1 = "email";
    public static final String LOGIN_PARAM2 = "password";

    public static final int ERROR_CREATE_ACCOUNT_INVALID_MAIL = 1;
    public static final int ERROR_CREATE_ACCOUNT_INVALID_PASS = 2;
    public static final int ERROR_CREATE_ACCOUNT_INVALID_USER = 3;

    public static final String CGU = "https://polyvox.fr/gtu";

    private APIUrl(){}
}
