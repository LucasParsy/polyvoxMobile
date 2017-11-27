package com.tuxlu.polyvox.Utils;

/**
 * Created by tuxlu on 16/09/17.
 */

public class APIUrl {
    public static final String SCHEME = "https";
    public static final String AUTHORITY = "api.polyvox.fr";
    public static final String BASE_URL = SCHEME + "://" + AUTHORITY + "/";

    public static final String FAKE_BASE_URL = SCHEME + "://" + "fakeApi.polyvox.fr" + "/";

    public static final String INFO_CURRENT_USER = "users/currentUser";

    public static final String DISCOVER_ROOMS = "discoverRoom";
    public static final String SEARCH_ROOMS = "searchRoom";

    public static final String CREATE_ACCOUNT = "createAccount";
    public static final String INFO_ROOM = "infoRoom";
    public static final String INFO_USER = "users/";
    public static final String VIDEO_STREAM = "video";
    public static final String COOKIE_HEADER_RECEIVE = "set-cookie";
    public static final String COOKIE_HEADER = "Cookie";
    public static final String INVALID_CREDENTIALS_JSON = "invalidCredentials";
    //public static final String GET_CHAT = "getChat";
    //public static final String SEND_CHAT = "sendChat";


    public static final Integer TOKEN_DENIED_CODE = 401;

    public static final String LOGIN = "users/login";
    public static final String LOGIN_PARAM1 = "email";
    public static final String LOGIN_PARAM2 = "password";
    public static final Integer LOGIN_INVALID_USER_CODE = 404;

    public static final String REGISTER = "users";
    public static final String REGISTER_PARAM1 = "email";
    public static final String REGISTER_PARAM2 = "password";
    public static final String REGISTER_PARAM3 = "userName";
    public static final String REGISTER_PARAM4 = "birthday";
    public static final String REGISTER_PARAM5 = "cgu";
    public static final Integer REGISTER_ERROR_CODE = 422;
    public static final String REGISTER_MAIL_ERROR = "Email";
    public static final String REGISTER_LOGIN_ERROR = "Username";

    public static final String MAIL_SEND = "users/send";

    public static final int ERROR_CREATE_ACCOUNT_INVALID_MAIL = 1;
    public static final int ERROR_CREATE_ACCOUNT_INVALID_PASS = 2;
    public static final int ERROR_CREATE_ACCOUNT_INVALID_USER = 3;



    public static final String SEARCH = "search";
    public static final String SEARCH_PARAM1 = "query";
    public static final String SEARCH_USER_JSONOBJECT = "data";
    public static final String SEARCH_USER_NAME = "userName";
    public static final String SEARCH_USER_IMAGE_URL = "picture";

    public static final String CGU = "https://polyvox.fr/gtu";

    private APIUrl(){}
}
