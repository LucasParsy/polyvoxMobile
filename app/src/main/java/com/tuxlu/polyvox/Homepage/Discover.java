package com.tuxlu.polyvox.Homepage;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.APIUrl;
import com.tuxlu.polyvox.Utils.DummyAPIServer;
import com.tuxlu.polyvox.Utils.LoadingUtils;
import com.tuxlu.polyvox.Utils.VHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Discover extends Fragment {
    private static final String TAG = "Discover";

    public Discover() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_discover, container, false);
        final Context context = getContext();
       final RecyclerView grid = (RecyclerView) view.findViewById(R.id.recycleView);

        grid.setHasFixedSize(true);

        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.homepage_rooms_row_number));

        SpaceItemDecoration dividerItemDecoration = new SpaceItemDecoration(8);
        grid.addItemDecoration(dividerItemDecoration);

        grid.setLayoutManager(layoutManager);

        String url = APIUrl.BASE_URL + APIUrl.DISCOVER_ROOMS;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        grid.setAdapter(new DiscoverAdapter(parseRoomboxJSON(response)));
                        LoadingUtils.EndLoadingView(view);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        VHttp.getInstance(context).addToRequestQueue(jsObjRequest);

        return view;
    }

    List<RoomBox> parseRoomboxJSON(JSONObject infoJson) {
        List<RoomBox> data = new ArrayList<>();
        JSONArray jArray = null;

        try {
            jArray = infoJson.getJSONArray("rooms");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jArray.length(); i++) {
            RoomBox rb = new RoomBox();
            try {
                JSONObject obj = jArray.getJSONObject(i);
                rb.name = obj.getString("title");
                rb.imageUrl = obj.getString("picture");
                rb.viewers = obj.getInt("viewers");
                rb.roomID = obj.getInt("roomID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            data.add(rb);
        }
        return data;
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mSpaceHeight;

        public SpaceItemDecoration(int mSpaceHeight) {
            this.mSpaceHeight = mSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mSpaceHeight;
            outRect.top = mSpaceHeight;
            outRect.left = mSpaceHeight;
            outRect.right = mSpaceHeight;
        }
    }
}
