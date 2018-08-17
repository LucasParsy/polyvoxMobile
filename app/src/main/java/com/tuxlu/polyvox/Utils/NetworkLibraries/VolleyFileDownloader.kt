package com.tuxlu.polyvox.Utils.NetworkLibraries

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.tuxlu.polyvox.Utils.UtilsTemp


/**
 * Created by tuxlu on 03/01/18.
 */

class VolleyFileDownloader(method: Int, val mUrl: String, val listener: Response.Listener<Pair<String, String>>, errorListener: Response.ErrorListener) :
        Request<ByteArray>(method, mUrl, errorListener) {

    var name ="file"
    var type =""

    init {
        setShouldCache(false)
    }


    override fun deliverResponse(response: ByteArray) {
        val pair = Pair<String, String>(name, type)
        listener.onResponse(pair)
    }

    override fun parseNetworkResponse(response: NetworkResponse) : Response<ByteArray>?
    {
        if (response.data != null)
        {
            type = response.headers.get("Content-Type")!!
            val disposition = response.headers.get("Content-Disposition")
            if (!disposition.isNullOrBlank())
            {
                //Content-Disposition: attachment; filename="NAME"
                val del: Int = disposition!!.indexOf('"')
                if (del != -1)
                    name = disposition.substring(del +1, disposition.length - 1)
            }
            else {
                val start: Int = url.lastIndexOf("/") + 1
                name = url.substring(start)
            }
            val file = UtilsTemp.getPath(name)
            file.writeBytes(response.data)
            response.headers.put("name", name)
        }
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
    }
}