package com.tuxlu.polyvox.Homepage

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Search.SearchRoomRecycler
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

open class DiscoverRoomRecycler : SearchRoomRecycler() {
    override val layoutObjectId: Int = R.layout.info_discover_room
    override val layoutListId: Int = R.layout.fragment_recycler_view_refreshable

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    protected open val requestUrl = APIUrl.DISCOVER_ROOMS
    protected open val usesAPI = false

    override fun setLayoutManager(): RecyclerView.LayoutManager =
            GridLayoutManager(activity, resources.getInteger(R.integer.homepage_rooms_row_number))

    open fun parseTopData(response: JSONObject) {
        val arr: JSONArray = response.getJSONObject("data").getJSONArray("running")
        val arr2: JSONArray = response.getJSONObject("data").getJSONArray("created")
        for (i in 0 until arr2.length())
            arr.put(arr2[i])

        this.add(arr, true)
        //this.add(response.getJSONObject("data").getJSONArray("created"), false)
    }

    private fun updateRooms() {
        APIRequest.JSONrequest(rootView.context, Request.Method.GET, APIUrl.BASE_URL + requestUrl, usesAPI, null,
                { response ->
                    try {
                        parseTopData(response)
                        swipeRefreshLayout.isRefreshing = false
                        LoadingUtils.EndLoadingView(rootView)
                    } catch (ignored: JSONException) {
                    }
                }, { swipeRefreshLayout.isRefreshing = false; })

        if (!APIRequest.checkConnection(context, false))
            swipeRefreshLayout.isRefreshing = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = super.onCreateView(inflater, container, savedInstanceState)!!
        LoadingUtils.StartLoadingView(rootView, context)
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener { updateRooms() }
        updateRooms()
        return rootView
    }
}