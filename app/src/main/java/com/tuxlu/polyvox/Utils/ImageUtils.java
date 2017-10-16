package com.tuxlu.polyvox.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.tuxlu.polyvox.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by parsyl on 20/07/2017.
 */

public class ImageUtils {

    //Todo déplacer la fonction dans un fichier plus à propos
    public static void shareContent(Context context, String body, String url)
    {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, body);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share_with)));
    }

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
