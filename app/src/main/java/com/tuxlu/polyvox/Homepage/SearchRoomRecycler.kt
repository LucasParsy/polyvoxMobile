package com.tuxlu.polyvox.Homepage

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.volley.VolleyError
import com.android.volley.toolbox.NetworkImageView
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Room.Room
import com.tuxlu.polyvox.Utils.*
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by tuxlu on 15/11/17.
 */

open class SearchRoomBinder : DiscoverBinder() {

    override fun bind(holder: Adapter.ViewHolder<DiscoverBox>, item: DiscoverBox)
    {
        super.bind(holder, item)
    }

    override fun setClickListener(holder: Adapter.ViewHolder<DiscoverBox>, data: List<DiscoverBox>)
    {
        super.setClickListener(holder, data)
    }
}


open class SearchRoomRecycler() : DiscoverRecycler()
{
    override val layoutObjectId: Int = R.layout.search_room_info
    override var requestUrl : String = APIUrl.SEARCH_ROOMS //+ "?query=" + arguments.getString("query")

    override val binder = SearchRoomBinder()
    override val itemDecoration= SpaceItemDecoration()
    override val requestObjectName : String = "result"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            requestBody.put("query", arguments.getString("query"))
        } catch (e: JSONException) {
            Log.wtf(tag, e);
        }
    }

    override fun errorListener(error: VolleyError)
    {
        Log.wtf(tag, error);
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)
}