package com.example.tuxlu.polyvox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class HomepageInfo {
private ImageAdapter adapter;
    private  HomepageInfoThread dlThread;
    private   Handler handler;

    public HomepageInfo (Context context, GridView view) {
        adapter = new ImageAdapter(context);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();
            }
        };

        dlThread = new HomepageInfoThread(adapter, handler, context);
        dlThread.start();

        setGridView(view);
    }

    void setGridView(GridView view)
    {
        view.setAdapter(adapter);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(adapter.this , "" + position, Toast.LENGTH_SHORT).show();
           }
        });
    }
}
