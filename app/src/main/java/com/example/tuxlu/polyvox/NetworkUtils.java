package com.example.tuxlu.polyvox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
