package com.tuxlu.polyvox;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.tuxlu.polyvox.Utils.MyDateUtils;
import com.tuxlu.polyvox.Utils.UtilsTemp;

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
    public void testTimeMethods() throws ParseException, InterruptedException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Locale loc = Locale.FRENCH;
        Resources res = appContext.getResources();
        res.getConfiguration().setLocale(loc);
        String format = "yyyy-MM-dd HH:mm";

        Date currentDate = new SimpleDateFormat(format, loc).parse("2018-09-06 19:09");
        Date today = new SimpleDateFormat(format, loc).parse("2018-09-06 10:00");
        Date yesterday = new SimpleDateFormat(format, loc).parse("2018-09-05 10:00");
        Date monday = new SimpleDateFormat(format, loc).parse("2018-09-03 10:00");
        Date thisYear = new SimpleDateFormat(format, loc).parse("2018-06-01 10:00");
        Date lastYear = new SimpleDateFormat(format, loc).parse("2017-11-01 10:00");

        assertEquals("10:00", MyDateUtils.getPrettyDate(today, res, currentDate));
        assertEquals("hier, 10:00", MyDateUtils.getPrettyDate(yesterday, res, currentDate));
        assertEquals("lundi, 10:00", MyDateUtils.getPrettyDate(monday, res, currentDate));
        assertEquals("1 juin", MyDateUtils.getPrettyDate(thisYear, res, currentDate));
        assertEquals("1 novembre 2017", MyDateUtils.getPrettyDate(lastYear, res, currentDate));


        loc = Locale.ENGLISH;
        res = appContext.getResources();
        res.getConfiguration().setLocale(loc);
        res.updateConfiguration(res.getConfiguration(), null);
        TimeUnit.SECONDS.sleep(1);


        assertEquals("yesterday, 10:00", MyDateUtils.getPrettyDate(yesterday, res, currentDate));
        assertEquals("June 1", MyDateUtils.getPrettyDate(thisYear, res, currentDate));
        assertEquals("Monday, 10:00", MyDateUtils.getPrettyDate(monday, res, currentDate));

        assertEquals("Tue Sep 04 12:09:59 GMT+02:00 2018",
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", loc).parse("2018-09-04T10:09:59.090Z").toString());
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.tuxlu.polyvox", appContext.getPackageName());

        ContentResolver res = appContext.getContentResolver();

        String mime = "";
        mime = res.getType(Uri.parse("https://jeuxvideo.com/news"));
        assertEquals(null, mime);

        //mime = res.getType(Uri.parse("https://jeuxvideo.com/news.pdf"));
        //assertEquals("application/pdf", mime);

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
