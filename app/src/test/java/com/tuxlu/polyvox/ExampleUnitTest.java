package com.tuxlu.polyvox;

import android.content.ContentResolver;

import org.junit.Test;

import java.net.URLConnection;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void substring_manip() throws Exception {
        String name ="";
        String extension = "";
        String disposition = "attachment; filename=\"NAME\"";
        int del = disposition.indexOf('"');
        if (del != -1)
            name = disposition.substring(del +1, disposition.length() - 1);

        assertEquals("NAME", name);

        name = "test.jpg";
        int point = name.lastIndexOf(".");
        if (point != -1)
            extension = name.substring(point);
        if (point != -1)
            name = name.substring(0, point);

        assertEquals("test", name);
        assertEquals(".jpg", extension);


        name = "test2";
        extension = "";
        point = name.lastIndexOf(".");
        if (point != -1)
            extension = name.substring(point);
        if (point != -1)
            name = name.substring(0, point);

        assertEquals("test2", name);
        assertEquals("", extension);


    }


    @Test
    public void guessContentType_tester() throws Exception {

        String mime = "";
        mime =URLConnection.guessContentTypeFromName("https://jeuxvideo.com/news");
        assertEquals(null, mime);

        mime = URLConnection.guessContentTypeFromName("https://jeuxvideo.com/news.pdf");
        assertEquals("application/pdf", mime);

        mime = URLConnection.guessContentTypeFromName("https://jeuxvideo.com/news.png");
        assert (mime.startsWith("image/"));
        mime = URLConnection.guessContentTypeFromName("https://jeuxvideo.com/news.jpg");
        assert (mime.startsWith("image/"));
        mime = URLConnection.guessContentTypeFromName("https://jeuxvideo.com/news.jpeg");
        assert (mime.startsWith("image/"));
        mime = URLConnection.guessContentTypeFromName("https://jeuxvideo.com/news.tiff");
        assert (mime.startsWith("image/"));


        mime = URLConnection.guessContentTypeFromName("https://jeuxvideo.com/news.xls");
        assertEquals(null, mime);
    }

}