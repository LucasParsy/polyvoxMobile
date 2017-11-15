package com.tuxlu.polyvox.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;

import com.tuxlu.polyvox.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by parsyl on 20/07/2017.
 */

public class ImageUtils {

    //Todo déplacer les fonction dans un fichier plus à propos
    public static void shareContent(Context context, String body, String url)
    {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, body);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share_with)));
    }

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
