package com.tuxlu.polyvox.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.tuxlu.polyvox.R;

/**
 * Created by tuxlu on 09/09/17.
 */

public class LoadingUtils {

    private static int delay = 5000;
    private static Handler handler = new Handler();

    public static void StartLoadingView(final View view, final Context context)
    {
        Button btn = (Button) view.findViewById(R.id.noWifiButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        handler.postDelayed(new Runnable(){
            public void run(){
                view.findViewById(R.id.noWifiLinear).setVisibility(View.VISIBLE);
                view.findViewById(R.id.noWifiProgressBar).setVisibility(View.INVISIBLE);
            }
        }, delay);
    }

    public static void endNoWifiView(View view)
    {
        view.findViewById(R.id.noWifiLinear).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.noWifiProgressBar).setVisibility(View.VISIBLE);
        handler.removeCallbacksAndMessages(null);
    }

    public static void EndLoadingView(final View view)
    {
        handler.removeCallbacksAndMessages(null);
        view.findViewById(R.id.noWifiLayout).setVisibility(View.GONE);
    }

}

