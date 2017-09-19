package com.tuxlu.polyvox.Homepage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.VHttp;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by tuxlu on 19/09/17.
 */

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.ViewHolder> {

    private static final String TAG = "DiscoverAdapter";
    private List<RoomBox> data;
    private VHttp vHttp;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public NetworkImageView image; //NetworkImageView?
        public TextView name;
        public TextView viewers;
        private FrameLayout layout;

        public ViewHolder(View v) {
            super(v);
            image = (NetworkImageView) v.findViewById(R.id.discover_room_picture);
            name = (TextView) v.findViewById(R.id.discover_room_name);
            viewers = (TextView) v.findViewById(R.id.discover_room_viewers);
            layout = (FrameLayout) v.findViewById(R.id.discover_room_frame);

            //clickListener
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Home.class);
                    intent.putExtra("id", getAdapterPosition());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    public void getImage(String url, final ImageView v) {
        if (TextUtils.isEmpty(url)) return;

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                v.setImageBitmap(response);
            }
        }, 0, 0, null, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error- " + error.getMessage());
            }
        });
        VHttp.getInstance(v.getContext()).addToRequestQueue(imageRequest);
    }



    public DiscoverAdapter(List<RoomBox> ndata) {
        data = ndata;
    }

    @Override
    public DiscoverAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discover_room_info , parent, false);
        // set the view's size, margins, paddings and layout parameters
        vHttp = VHttp.getInstance(parent.getContext());
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RoomBox box = data.get(position);

        holder.viewers.setText(String.valueOf(box.viewers));
        holder.name.setText(box.name);
        holder.image.setImageUrl(box.imageUrl, vHttp.getImageLoader());
        //getImage(box.imageUrl, holder.image);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}