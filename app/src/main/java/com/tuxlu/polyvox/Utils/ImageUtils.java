package com.tuxlu.polyvox.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.tuxlu.polyvox.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by parsyl on 20/07/2017.
 */

public class ImageUtils {

    //todo: move theses function in more appropriate file
    public static void shareContent(Context context, String body, String url)
    {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, body);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share_with)));
    }

    //todo: move theses function in more appropriate file
    public static void checkNetworkError(Context context, VolleyError error) {
        if (error instanceof NetworkError ||
                error instanceof AuthFailureError ||
                error instanceof TimeoutError ||
                error instanceof ParseError)
        {
            showToast(context, context.getString(R.string.no_wifi_home), R.style.ToastStyleNoWifi);
        }
        else if (error instanceof ServerError)
            showToast(context, context.getString(R.string.no_server), R.style.ToastStyleNoServer);
    }

    //todo: move theses function in more appropriate file
    public static void showToast(Context context, String text, int layout) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        TextView v = toast.getView().findViewById(android.R.id.message);

        //StyleableToast toast = StyleableToast.makeText(context, text, Toast.LENGTH_LONG, layout);
        //TextView v = toast.getStyleableToast().getView().findViewById(android.R.id.message);

        if (v != null)
            v.setGravity(Gravity.CENTER);
        toast.show();
    }

    //todo: move theses function in more appropriate file
    public static boolean setNotificationSilentState(Context context, int mode) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (manager == null)
            return false;

        //state: true -> silent, false -> normal
        if (manager.getRingerMode() == mode)
            return true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null)
                return false;

            if (!notificationManager.isNotificationPolicyAccessGranted()) {

                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                context.startActivity(intent);
                return false;
            }
        }
        manager.setRingerMode(mode);
        return true;

            /*
            la fonction prend un int "mode" en paramètre.
            pour mettre en silencieux l'appli, puis revenir au mode précedemment défini,
            il faut d'abord enregistrer le mode actuel.

            ex: avant d'appeler cette fonction pour mute:

            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (manager != null);
            int mode  = manager.getRingerMode();
            setNotificationSilentState(this, AudioManager.RINGER_MODE_SILENT)
            ...
            //revenir au mode précédent:
            setNotificationSilentState(this, mode)


            Todo: comment bien faire: prendre l'activité qui appelle la fonction, et un int en paramètres
            lancer l'activité de demande de permission, qui va appeler une callback sur l'activité originelle
            redemander la permission dans cette callback.

            activity.startActivityForResult(intent), REQUESTCODENUMBER);

            @Override
                    ask again for permission
                }
            }
            */


    }

/*
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Check which request we're responding to
                if (requestCode == REQUESTCODENUMBER) {
*/

    public static void bitmapToFile(Bitmap myBmp, String filename, Context context)
    {
        File f = new File(context.getCacheDir(), filename + ".png");
        try {
            f.createNewFile();
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        myBmp.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
