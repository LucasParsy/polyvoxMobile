package com.example.tuxlu.polyvox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by parsyl on 19/07/2017.
 */

//TODO changer Thread en Activity, et ajouter loading spinner.

public class DiscoverTask extends AsyncTask<Void ,Integer, Void> {
    private static final String TAG = "DiscoverTask";
    ImageAdapter adapter;
    Handler handler;
    Context context;
    View view;
    //TODO enlever  context lorsque API finalisée: utilisé juste pour récupérer JSON de test.

    public DiscoverTask(ImageAdapter nadap, Handler nhand, View nview,  Context ncont)
    {
        handler = nhand;
        adapter = nadap;
        context = ncont;
        view = nview;
    }

    public Void doInBackground(Void... none)
    {
        JSONObject infoJson = null;
        JSONArray jArray = null;

        try {
            infoJson = new JSONObject(context.getString(R.string.sampleHomepageJSON));
            //infoJson = NetworkUtils.downloadJSON("https://polyvox.com/api/v1/homepage", context);
            jArray = infoJson.getJSONArray("rooms");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Home.hideNoConnectionBar();
        //Log.v(TAG, jArray.toString());

        for (int i=0; i < jArray.length(); i++)
        {
            RoomBox rb = new RoomBox();
            try {
                JSONObject obj = jArray.getJSONObject(i);
                rb.name = obj.getString("title");
                String picCache = obj.getString("picCacheID");
                rb.bitmap = ImageUtils.getImage(picCache, obj.getString("picture"), context);
                rb.viewers = obj.getInt("viewers");
                rb.roomID = obj.getInt("roomID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.addBox(rb);
            handler.sendMessage(handler.obtainMessage());
            publishProgress(i); //removes loading bar at 2 elements.
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        if (progress[0] == 0) {
            LoadingUtils.endNoWifiView(view);
        }

        if (progress[0] == 2) {
            LoadingUtils.EndLoadingView(view);
        }
    }

    protected Void onPostExecute() {
    return null;
    }



}