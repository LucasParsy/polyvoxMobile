package com.example.tuxlu.polyvox;

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
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by tuxlu on 09/09/17.
 */

public class PrivateChatList extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_private_chat_list, container, false);

        final Context context = getContext();
        LoadingUtils.StartLoadingView(view, context);

        //TODO utiliser des templates pour chaque tabs de la HomePage?
        /*


        final ImageAdapter adapter = new ImageAdapter(context);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                adapter.notifyDataSetChanged();
            }
        };

        //dlThread = new DiscoverTask(adapter, handler, view, context);
        //dlThread.execute();

        final ListView list = (ListView) view.findViewById(R.id.private_chat_list);
        setGridView(list, adapter, context);
        */
        return view;
    }


    void setGridView(final ListView list, final ImageAdapter adapter, final Context context)
    {
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(context , "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
