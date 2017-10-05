package com.tuxlu.polyvox.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by parsyl on 19/07/2017.
 */


public class NetworkUtils  {

    public static boolean isConnected(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isAPIConnected(Context context)
    {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccounts();
        return ( accounts.length != 0 );
    }


    public static String getToken(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccounts();
        if( accounts.length == 0 ) {
            return "";
        }
        Bundle res;
        try {
            res = am.getAuthToken(accounts[0],
                    context.getString(R.string.account_type), null,  false, null, null).getResult();
            if (res.containsKey(AccountManager.KEY_INTENT)) {
                Intent authIntent = res.getParcelable(AccountManager.KEY_INTENT);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
       return res.get(AccountManager.KEY_AUTHTOKEN).toString();
    }


    public static void JSONrequest(final Context context, final int method, final String url,
                                   final boolean usesApi, final JSONObject body,
                                   final Response.Listener<JSONObject> listener,
                                   final Response.ErrorListener errorListener)
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String token = usesApi ? getToken(context) : null;
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (method, url, body, listener, errorListener){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        if (!usesApi)
                            return super.getHeaders();
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("token", token);
                        return params;
                    }
                };
                VHttp.getInstance(context).addToRequestQueue(jsObjRequest);
            }
        });
    }

    public static InputStream downloadStream(String urlStr, Context context)
    {

        InputStream in = null;

        while (!isConnected(context)) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try
        {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.connect();
            in = httpConn.getInputStream();
        }
        catch (MalformedURLException e)
        {
            try{if(in != null)in.close();}catch(Exception e2){}
            e.printStackTrace();
        }
        catch (IOException e)
        {
            try{if(in != null)in.close();}catch(Exception e2){}
            e.printStackTrace();
        }
        return  in;
    }

    @Nullable
    public static JSONObject downloadJSON(String url, Context context) {
        InputStream in = downloadStream(url, context);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            return new JSONObject(sb.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try{if(in != null)in.close();}catch(Exception squish){}
        }
        return null;
    }
}
