package com.tuxlu.polyvox.Utils

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by tuxlu on 12/11/17.
 */


abstract class IRequestRecycler<T: Any>() : Fragment() {

    abstract val layoutListId: Int
    abstract val layoutObjectId: Int
    abstract val recycleId: Int
    abstract val requestUrl : String
    abstract val requestBody: JSONObject
    abstract val usesAPI : Boolean;
    abstract val requestObjectName : String

    abstract val itemDecoration: RecyclerView.ItemDecoration
    abstract val binder: ViewHolderBinder<T>

    private var adapter: Adapter<T>? = null;

    abstract fun errorListener(error: VolleyError)
    abstract fun fillDataObject(json: JSONObject): T
    abstract fun setLayoutManager(): RecyclerView.LayoutManager
    /*{
        return final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.homepage_rooms_row_number))
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(layoutListId, container, false)
        val recycler = view.findViewById<View>(recycleId) as RecyclerView
        recycler.setHasFixedSize(true)


        val layoutManager = setLayoutManager()
        recycler.addItemDecoration(itemDecoration)
        recycler.layoutManager = layoutManager

        adapter = Adapter(context, ArrayList(), layoutObjectId, binder)
        recycler.adapter = adapter
        request(requestUrl, requestBody, true, view)
        return view
    }


    fun clear()
    {
        adapter?.clear()
        adapter?.notifyDataSetChanged()
    }

    internal fun request(url: String, body: JSONObject, append: Boolean, view: View? = null)
    {
        val eListen = Response.ErrorListener { error -> errorListener(error) }
        NetworkUtils.JSONrequest(context,Request.Method.GET, APIUrl.BASE_URL  + url, usesAPI, body, Response.Listener<JSONObject> { response ->
            if (!append)
                adapter?.clear()
            adapter?.add(parseJSON(response))
            adapter?.notifyDataSetChanged()
            if (view != null)
                LoadingUtils.EndLoadingView(view)
        }, eListen)

    }

    internal fun parseJSON(infoJson: JSONObject): MutableList<T> {
        val data = ArrayList<T>()
        var jArray: JSONArray? = null

        try {
            jArray = infoJson.getJSONArray(requestObjectName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        if (jArray == null)
            return data
        for (i in 0 until jArray.length()) {
            val obj : T
            try {
                val json = jArray.getJSONObject(i)
                obj = fillDataObject(json);
                data.add(obj)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return data
    }
}
