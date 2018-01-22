package com.tuxlu.polyvox.Utils.NetworkLibraries;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.API.APIUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tuxlu on 16/09/17.
 */

public class DummyAPIServer {

    private JSONObject roomList;
    private JSONObject userList;
    private JSONObject chatList;
    private JSONObject chatMessages;
    private JSONObject chatMessages2;
    private JSONObject chatMessageUpdate;
    private JSONObject fileList;
    private boolean messageUpdated = false;
    int userNum = 43;


    DummyAPIServer(Context context) {
        try {
            roomList = fileToJSON(R.raw.rooms, context);
            userList = fileToJSON(R.raw.users, context);
            chatList = fileToJSON(R.raw.chat_list, context);
            chatMessages = fileToJSON(R.raw.chat_messages, context);
            chatMessages2 = fileToJSON(R.raw.chat_messages2, context);
            chatMessageUpdate = fileToJSON(R.raw.chat_messages_new, context);
            fileList = fileToJSON(R.raw.files_list, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject fileToJSON(int id, Context context) throws JSONException {
        InputStream is = context.getResources().openRawResource(id);
        String jsonString = "";
        String line;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null)
                jsonString += line;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject(jsonString);
    }

    public HttpResponse dummyRequest(com.android.volley.Request<?> request,
                                     Map<String, String> additionalHeaders) throws AuthFailureError, JSONException {
        String url = request.getUrl();
        JSONObject body = null;
        url = url.substring(APIUrl.BASE_URL.length());
        int slash = url.indexOf("/");
        String endpoint = url;
        if (slash != -1)
            endpoint = url.substring(0, slash);
        try {
            byte[] rbody = request.getBody();
            if (rbody != null) {
                String sbod = new String(rbody);
                body = new JSONObject(sbod);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        switch (url) {
            case APIUrl.DISCOVER_ROOMS:
                return discoverRequest();
            case APIUrl.SEARCH_ROOMS:
                return searchRequest(body);
            case APIUrl.LOGIN:
                return loginRequest(body);
            case APIUrl.CHAT + APIUrl.FAKE_CHAT_NAME:
                JsonObjectRequest jreq = (JsonObjectRequest) request;
                JSONObject obj = new JSONObject(new String(jreq.getBody()));
                if (obj.getInt(APIUrl.CHAT_PARAM) == 2)
                    return makeHttpResponse(chatMessages2, 200);
                else
                    return makeHttpResponse(chatMessages, 200);
            case APIUrl.CHAT_UPDATE + APIUrl.FAKE_CHAT_NAME:
                if (!messageUpdated) {
                    messageUpdated = true;
                    HttpResponse res = makeHttpResponse(chatMessageUpdate, 200);
                    chatMessageUpdate.getJSONObject("data").put("messages", new JSONArray());
                    return res;
                } else
                    return makeHttpResponse(chatMessageUpdate, 200);
            case APIUrl.LIST_USERS_CHAT:
                return makeHttpResponse(chatList, 200);
            case APIUrl.CREATE_ACCOUNT:
                return createAccountRequest(body);
            case APIUrl.INFO_ROOM:
                return infoRoomAndUserRequest(body, roomList, "rooms");
            case APIUrl.INFO_USER:
                return infoRoomAndUserRequest(body, userList, "users");
            case APIUrl.ROOM_FILE_LIST:
                return makeHttpResponse(fileList, 200);
        }
        return makeHttpResponse(new JSONObject(), 404);
    }

    private HttpResponse makeHttpResponse(JSONObject obj, int code) {
        //BasicStatusLine stats = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), code, "");
        String objStr = obj.toString();
        HttpResponse res = null;
        List<Header> headers = new ArrayList<Header>();
        headers.add(new Header("Access-Control-Allow-Credentials", "true"));
        headers.add(new Header("Access-Control-Allow-Headers", "content-type"));
        headers.add(new Header("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE"));
        headers.add(new Header("Access-Control-Allow-Origin", "https://polyvox.fr"));
        headers.add(new Header("Connection", "keep-alive"));
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Content-Length", String.valueOf(objStr.length())));


        try {
            res = new HttpResponse(code, headers, objStr.length(),
                    new ByteArrayInputStream(objStr.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    private HttpResponse discoverRequest() {
        return makeHttpResponse(roomList, 200);
    }

    private HttpResponse loginRequest(JSONObject obj) {
        String login;
        String password;
        JSONObject valid;
        JSONObject error;

        try {
            valid = new JSONObject("{\"token\": \"Black\", \"refreshToken\": \"refresh\"}");
            error = new JSONObject("{\"reason\": \"none\"}");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        try {
            login = obj.getString("login");
            password = obj.getString("password");
            JSONArray rooms = userList.getJSONArray("users");
            for (int i = 0; i < rooms.length(); i++) {
                JSONObject info = rooms.getJSONObject(i);
                if (info.getString("name").equals(login)) {
                    if (info.getString("pass").equals(password))
                        return makeHttpResponse(valid, 200);
                    else
                        return makeHttpResponse(error, 400);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return makeHttpResponse(error, 400);
        }
        return makeHttpResponse(error, 400);
    }

    private HttpResponse createAccountRequest(JSONObject obj) {
        String login;
        String password;
        String mail;
        JSONObject valid;
        JSONObject error;

        try {
            valid = new JSONObject("{success: true}");
            error = new JSONObject("{success: false}");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        try {
            login = obj.getString("login");
            password = obj.getString("password");
            mail = obj.getString("mail");

            if (!mail.contains("@") || mail.contains("@yopmail.com")) {
                error.put("reason", APIUrl.ERROR_CREATE_ACCOUNT_INVALID_MAIL);
                return makeHttpResponse(error, 400);
            }

            if (password.length() < 3) {
                error.put("reason", APIUrl.ERROR_CREATE_ACCOUNT_INVALID_PASS);
                return makeHttpResponse(error, 400);
            }

            JSONArray users = userList.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                JSONObject info = users.getJSONObject(i);
                if (info.getString("name").equals(login)) {
                    error.put("reason", APIUrl.ERROR_CREATE_ACCOUNT_INVALID_USER);
                    return makeHttpResponse(error, 400);
                }
            }

            JSONObject nuser = new JSONObject("{\"premium\": false, \"friendlist\" : [], " +
                    "\"currentRoom\": 0 }");

            nuser.put("id", userNum++);
            nuser.put("name", login);
            nuser.put("pass", password);
            users.put(nuser);
            return makeHttpResponse(valid, 200);


        } catch (JSONException e) {
            e.printStackTrace();
            return makeHttpResponse(error, 400);
        }
    }

    private HttpResponse infoRoomAndUserRequest(JSONObject obj, JSONObject jobj, String name) {
        int id;

        try {
            id = obj.getInt("query");
            JSONArray arr = jobj.getJSONArray(name);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject info = arr.getJSONObject(i);
                if (info.getInt("id") == id)
                    return makeHttpResponse(info, 200);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return makeHttpResponse(new JSONObject(), 400);
    }

    private HttpResponse searchRequest(JSONObject obj) {
        String req;
        JSONObject res = new JSONObject();
        JSONArray arr = new JSONArray();

        try {
            req = obj.getString("query");
            JSONArray rooms = roomList.getJSONArray("rooms");
            for (int i = 0; i < rooms.length(); i++) {
                JSONObject info = rooms.getJSONObject(i);
                if (info.getString("title").toLowerCase().contains(req.toLowerCase()))
                    arr.put(info);
            }
            res.put("result", arr);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return makeHttpResponse(res, 200);
    }

}
