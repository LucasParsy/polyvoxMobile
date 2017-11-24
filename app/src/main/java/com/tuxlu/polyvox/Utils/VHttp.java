package com.tuxlu.polyvox.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by tuxlu on 19/09/17.
 */

public class VHttp {
    private static VHttp mInstance;
    private RequestQueue mRequestQueue;
    private final ImageLoader mImageLoader;

    private VHttp(Context context) {
        final int cacheSize = 20;

        //todo enlever Dummy quand connexion API faite
        DummyAPIServer dummy = new DummyAPIServer(context);
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext(),
                new OkHttp3Stack(dummy));

        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(cacheSize);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VHttp getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VHttp(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
