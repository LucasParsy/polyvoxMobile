/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2015 Circle Internet Financial
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.tuxlu.polyvox.Utils.NetworkLibraries;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.HurlStack;
import com.tuxlu.polyvox.Utils.API.APIUrl;

import java.io.IOException;
import java.util.Map;

/**
 * OkHttp backed {@link com.android.volley.toolbox.HttpStack HttpStack} that does not
 * use okhttp-urlconnection
 */
public class HurlWithDummyStack extends HurlStack {

    private final DummyAPIServer dummy;

    public HurlWithDummyStack(DummyAPIServer ndummy) {
        dummy = ndummy;
    }

    @Override
    public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {
        if (//request.getUrl().startsWith(APIUrl.BASE_URL + APIUrl.DISCOVER_ROOMS) ||
                request.getUrl().startsWith(APIUrl.BASE_URL + APIUrl.ROOM_FILE_LIST) ||
                request.getUrl().startsWith(APIUrl.FAKE_BASE_URL))
            try {
                return dummy.dummyRequest(request, additionalHeaders);
            } catch (Exception ignored) {
            }
        return super.executeRequest(request, additionalHeaders);
    }
}