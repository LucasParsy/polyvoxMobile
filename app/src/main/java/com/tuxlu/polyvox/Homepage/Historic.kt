package com.tuxlu.polyvox.Homepage

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Room.RoomBase
import com.tuxlu.polyvox.Room.RoomHistoric
import com.tuxlu.polyvox.Search.RoomSearchBinder
import com.tuxlu.polyvox.Search.RoomSearchResult
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.MyDateUtils
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.UtilsTemp
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat

class HistoricRoomBinder(activity: FragmentActivity) : RoomSearchBinder(activity) {

    override fun bind(holder: Adapter.ViewHolder<RoomSearchResult>, item: RoomSearchResult) {
        super.bind(holder, item)
        holder.v.findViewById<LinearLayout>(R.id.layoutDate).visibility = View.VISIBLE
        holder.v.findViewById<TextView>(R.id.infoRoomDate).text = item.date
        holder.v.findViewById<ImageView>(R.id.logoSpeakers).visibility = View.INVISIBLE
    }

    override fun setClickListener(holder: Adapter.ViewHolder<RoomSearchResult>, data: MutableList<RoomSearchResult>) {
        val context = holder.v.context
        val clickListener = View.OnClickListener { _ ->

            val image = holder.v.findViewById<ImageView>(R.id.infoRoomPicture)
            val intent = Intent(context, RoomHistoric::class.java)
            val b = Bundle()
            b.putString("title", data[holder.adapterPosition].name)
            b.putString("token", data[holder.adapterPosition].token)
            b.putString("imageUrl", data[holder.adapterPosition].imageUrl)
            intent.putExtras(b)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, image, ViewCompat.getTransitionName(image)!!)
            context.startActivity(intent, options.toBundle())
        }
        holder.v.findViewById<View>(R.id.infoRoomLayout).setOnClickListener(clickListener)
    }
}

class HistoricRoomRecycler : DiscoverRoomRecycler() {
    override val requestUrl = APIUrl.HISTORIC
    override val usesAPI = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = HistoricRoomBinder(requireActivity())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun parseTopData(response: JSONObject) {
        this.add(response.getJSONArray("data"), true)
    }

    override fun fillDataObject(json: JSONObject): RoomSearchResult {
        val res = super.fillDataObject(json)
        try {
            val dateString = json.getString("createdAt")
            val parsedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX",
                    UtilsTemp.getLocale(resources)).parse(dateString)
            res.date = MyDateUtils.getPrettyDate(parsedDate, resources)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return res
    }

}