package com.tuxlu.polyvox;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.tuxlu.polyvox", appContext.getPackageName());

        ContentResolver res = appContext.getContentResolver();

        String mime = "";
        mime = res.getType(Uri.parse("https://jeuxvideo.com/news"));
        assertEquals(null, mime);

        mime = res.getType(Uri.parse("https://jeuxvideo.com/news.pdf"));
        assertEquals("application/pdf", mime);

        mime = res.getType(Uri.parse("https://jeuxvideo.com/news.png"));
        assert (mime.startsWith("image/"));
        mime = res.getType(Uri.parse("https://jeuxvideo.com/news.jpg"));
        assert (mime.startsWith("image/"));
        mime = res.getType(Uri.parse("https://jeuxvideo.com/news.jpeg"));
        assert (mime.startsWith("image/"));
        mime = res.getType(Uri.parse("https://jeuxvideo.com/news.tiff"));
        assert (mime.startsWith("image/"));


        mime = res.getType(Uri.parse("https://jeuxvideo.com/news.xls"));
        assertEquals(null, mime);



    }
}
