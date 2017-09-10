package com.tuxlu.polyvox.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by parsyl on 20/07/2017.
 */

public class ImageUtils {

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

    //TODO supprimer le cache des anciennes images;

    public static Bitmap getImage(String id, String urlStr, Context context)
    {
        File fpath = new File(context.getCacheDir() + "/" + id + ".png");
        if(fpath.exists()){
            return BitmapFactory.decodeFile(fpath.getPath());
        }
        InputStream stream = NetworkUtils.downloadStream(urlStr, context);
        Bitmap bmpimg = BitmapFactory.decodeStream(stream);
        try{if(stream != null)stream.close();}catch(Exception e){ e.printStackTrace(); }
        bitmapToFile(bmpimg, id, context);
        return bmpimg;
    }


}
