package com.example.tuxlu.polyvox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*
public class HomepageInfo extends Activity {
    HomepageInfoDownloader dlThread;
    Handler handler = new Handler();



    @Override
     public void handleMessage(Message msg) {
        myAdapter.notifyDataSetChanged();
     }

};
*/



public class ImageAdapter extends BaseAdapter {

        private static final String TAG = "ImageAdapter";
        private final List<RoomBox> boxes = new ArrayList<RoomBox>();
        private final LayoutInflater mInflater;

        public ImageAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public void addBox(RoomBox rb) {
            boxes.add(rb);
        }

        @Override
        public int getCount() {
            return boxes.size();
        }

        @Override
        public RoomBox getItem(int i) {
            return boxes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            TextView name;

            if (v == null) {
                v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }
            picture = (ImageView) v.getTag(R.id.picture);
            name = (TextView) v.getTag(R.id.text);

            RoomBox item = getItem(i);

            picture.setImageBitmap(item.bitmap);
            name.setText(item.name);

            return v;
        }

}

