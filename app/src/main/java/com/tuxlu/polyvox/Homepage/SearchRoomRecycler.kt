package com.tuxlu.polyvox.Homepage

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.android.volley.VolleyError
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.*
import org.json.JSONObject

/**
 * Created by tuxlu on 15/11/17.
 */

open class SearchRoomBinder : DiscoverBinder() {

    override fun bind(holder: Adapter.ViewHolder<DiscoverBox>, item: DiscoverBox)
    {
        super.bind(holder, item)
    }

    override fun setClickListener(holder: Adapter.ViewHolder<DiscoverBox>, data: MutableList<DiscoverBox>)
    {
        super.setClickListener(holder, data)
    }
}


open class SearchRoomRecycler() : DiscoverRecycler()
{
    override val layoutObjectId: Int = R.layout.info_search_room
    override var requestUrl : String = APIUrl.SEARCH_ROOMS //+ "?query=" + arguments.getString("query")

    override val binder = SearchRoomBinder()
    override val itemDecoration= SpaceItemDecoration()
    override val requestObjectName : String = "result"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestBody.put("query", arguments!!.getString("query"))
    }

    fun search(query : String)
    {
        val body = JSONObject()
        body.put("query", query)
        request(requestUrl, body,false)
    }

    override fun errorListener(error: VolleyError)
    {
        Log.wtf(tag, error);
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)
}