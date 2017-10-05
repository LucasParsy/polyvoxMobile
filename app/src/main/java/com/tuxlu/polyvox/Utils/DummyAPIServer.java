package com.tuxlu.polyvox.Utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.tuxlu.polyvox.Homepage.Discover;
import com.tuxlu.polyvox.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

import okhttp3.Protocol;
import okhttp3.Response;

/**
 * Created by tuxlu on 16/09/17.
 */

public class DummyAPIServer {

    private JSONObject roomList;
    private JSONObject userList;
    int userNum = 43;


    DummyAPIServer(Context context) {
        try {
            roomList = fileToJSON(R.raw.rooms, context);
            userList = fileToJSON(R.raw.users, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject fileToJSON(int id, Context context) throws IOException, JSONException {
        InputStream is = context.getResources().openRawResource(id);
        String jsonString = "";
        String line;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null)
                jsonString += line;

        } catch (Exception e){
            e.printStackTrace();
        } finally{
            is.close();
        }
        return new JSONObject(jsonString);
    }

    public HttpResponse dummyRequest(com.android.volley.Request<?> request,
                                     Map<String, String> additionalHeaders) {
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

        switch (endpoint) {
            case APIUrl.DISCOVER_ROOMS:  return discoverRequest();
            case APIUrl.SEARCH_ROOMS:  return searchRequest(body);
            case APIUrl.LOGIN:  return loginRequest(body);
            case APIUrl.CREATE_ACCOUNT:  return createAccountRequest(body);
            case APIUrl.INFO_ROOM:  return infoRoomAndUserRequest(body, roomList, "rooms");
            case APIUrl.INFO_USER:  return infoRoomAndUserRequest(body, userList, "users");
        }
        return makeHttpResponse(new JSONObject(), 404);
    }

    private HttpResponse makeHttpResponse(JSONObject obj, int code) {
        BasicStatusLine stats = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), code, "");
        BasicHttpResponse res = new BasicHttpResponse(stats);
        try {
            res.setEntity(new StringEntity(obj.toString(), "UTF-8"));
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
            for (int i=0; i < rooms.length(); i++)
            {
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


        JSONArray arr = new JSONArray();

        try {
            login = obj.getString("login");
            password = obj.getString("password");
            mail = obj.getString("mail");

            if (!mail.contains("@") || mail.contains("@yopmail.com"))
            {
                error.put("reason", APIUrl.ERROR_CREATE_ACCOUNT_INVALID_MAIL);
                return makeHttpResponse(error, 400);
            }

            if (password.length() < 3)
            {
                error.put("reason", APIUrl.ERROR_CREATE_ACCOUNT_INVALID_PASS);
                return makeHttpResponse(error, 400);
            }

            JSONArray users = userList.getJSONArray("users");
            for (int i=0; i < users.length(); i++)
            {
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
            for (int i=0; i < arr.length(); i++)
            {
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
            for (int i=0; i < rooms.length(); i++)
            {
                JSONObject info = rooms.getJSONObject(i);
                if (info.getString("title").contains(req))
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
