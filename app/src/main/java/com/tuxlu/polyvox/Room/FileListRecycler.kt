package com.tuxlu.polyvox.Room

import android.app.Activity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.VolleyError
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableList
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import com.tuxlu.polyvox.Homepage.DiscoverBinder
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Fileloader
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.IRequestRecycler
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * Created by tuxlu on 19/01/18.
 */
open class FLRecycler : IRequestRecycler<FlUser>() {

    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val recycleId: Int = R.id.recycleView

    override val requestUrl: String = APIUrl.ROOM_FILE_LIST
    override val requestBody: JSONObject = JSONObject()
    override val usesAPI: Boolean = false
    override val requestObjectName: String = "data"

    //unused params
    override val layoutObjectId: Int = R.layout.info_discover_room
    override val binder = null;
    override val itemDecoration = null

    private lateinit var recycler: RecyclerView;


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(layoutListId, container, false)
        LoadingUtils.EndLoadingView(rootView)
        noResView = rootView.findViewById<View>(R.id.noResultsLayout)
        recycler = rootView.findViewById<RecyclerView>(recycleId)
        recycler.setHasFixedSize(true)
        val layoutManager = setLayoutManager()
        recycler.layoutManager = layoutManager
        LoadingUtils.StartLoadingView(view, context)
        request(requestUrl, requestBody, true, view)
        return rootView
    }


    override fun errorListener(error: VolleyError) {
        Log.wtf(tag, error)
    }

    override fun fillDataObject(json: JSONObject): FlUser {
        try {
            val name = json.getString("userName")
            val imageUrl = json.getString("url")
            val list: ArrayList<FlFile> = arrayListOf()

            val jsonList = json.getJSONArray("files")
            var file: JSONObject;
            for (i in 0 until jsonList.length()) {
                file = jsonList.getJSONObject(i)
                list.add(FlFile(file.getString("name"),
                        file.getString("url"),
                        file.getInt("size")))
            }
            return FlUser(name, imageUrl, list)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return FlUser("", "", arrayListOf());
    }

    override fun add(data: JSONArray, replace: Boolean)
    {
        val list = getAddData(data, replace)
        if (replace || list.size == 0)
            recycler.adapter = null
        if (list.size != 0) {
            val adapter = FilelistAdapter(parseJSON(data), activity!!)
            for (i in adapter.groups.size - 1 downTo 0) {
                if (!adapter.isGroupExpanded(adapter.groups[i]));
                    adapter.toggleGroup(adapter.groups[i])
            }
            recycler.adapter = adapter
        }
    }

    override fun clear()
    {
        recycler.adapter = null
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

}
