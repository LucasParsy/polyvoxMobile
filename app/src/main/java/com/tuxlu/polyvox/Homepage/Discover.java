package com.tuxlu.polyvox.Homepage;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.tuxlu.polyvox.R;
import com.tuxlu.polyvox.Utils.LoadingUtils;

public class Discover extends Fragment {
    private DiscoverTask dlThread;
    private   Handler handler;
    private static final String TAG = "Discover";
    private boolean finishedLoading = false;

    public Discover() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_discover, container, false);

        final Context context = getContext();
        LoadingUtils.StartLoadingView(view, context);



        final ImageAdapter adapter = new ImageAdapter(context);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();
            }
        };

        dlThread = new DiscoverTask(adapter, handler, view, context);
        dlThread.execute();

        final GridView grid  = (GridView) view.findViewById(R.id.gridview);
        setGridView(grid, adapter, context);

        return view;
    }

    void setGridView(final GridView grid, final ImageAdapter adapter, final Context context)
    {
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(context , "" + position, Toast.LENGTH_SHORT).show();
           }
        });
    }
}
