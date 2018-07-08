package com.tuxlu.polyvox.Search

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Room.Room
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.IRecycler
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import com.tuxlu.polyvox.Utils.UtilsTemp
import org.json.JSONException
import org.json.JSONObject
import android.support.v4.view.ViewCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.ViewGroup


/**
 * Created by tuxlu on 12/11/17.
 */

data class RoomSearchResult(var active: Boolean = true,
                            var name: String = "",
                            var imageUrl: String = "",
                            var tags: String = "",
                            var token: String = "",
                            var speakers: String = "")


open class RoomSearchBinder(val activity: FragmentActivity) : ViewHolderBinder<RoomSearchResult> {

    //private val random = SecureRandom()
    //private val defaultPictures = intArrayOf(R.drawable.default_room_picture1, R.drawable.default_room_picture2,
        //R.drawable.default_room_picture3, R.drawable.default_room_picture4)
    private val defaultPictures = intArrayOf(R.drawable.logo_grey)

    override fun bind(holder: Adapter.ViewHolder<RoomSearchResult>, item: RoomSearchResult) {
        if (!item.active)
            holder.v.visibility = View.GONE;
        holder.v.findViewById<TextView>(R.id.infoRoomName).text = item.name
        holder.v.findViewById<TextView>(R.id.infoRoomSubject).text = item.tags
        holder.v.findViewById<TextView>(R.id.infoRoomSpeakers).text = item.speakers.toString()


        val image = holder.v.findViewById<ImageView>(R.id.infoRoomPicture)

        ViewCompat.setTransitionName(image, item.name);

        //random.nextInt(4)
        if (!UtilsTemp.isStringEmpty(item.imageUrl))
            GlideApp.with(holder.v.context).load(item.imageUrl).placeholder(defaultPictures[0]).into(image)
        //else
            //image.setImageDrawable(holder.v.context.resources.getDrawable(defaultPictures[random.nextInt(4)]))
    }

    override fun setClickListener(holder: Adapter.ViewHolder<RoomSearchResult>, data: MutableList<RoomSearchResult>) {
        val context = holder.v.context
        val clickListener = View.OnClickListener { _ ->

            val image = holder.v.findViewById<ImageView>(R.id.infoRoomPicture)
            val intent = Intent(context, Room::class.java)
            val b = Bundle()
            b.putString("title", data[holder.adapterPosition].name)
            b.putString("token", data[holder.adapterPosition].token)
            b.putString("imageUrl", data[holder.adapterPosition].imageUrl)
            intent.putExtras(b)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, image, ViewCompat.getTransitionName(image))
            context.startActivity(intent, options.toBundle())
        }
        holder.v.findViewById<View>(R.id.infoRoomLayout).setOnClickListener(clickListener)
    }
}

open class SearchRoomRecycler : IRecycler<RoomSearchResult>() {

    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val layoutObjectId: Int = R.layout.info_search_room
    override val recycleId: Int = R.id.recycleView
    override val requestObjectName: String = APIUrl.SEARCH_USER_JSONOBJECT

    override var binder : ViewHolderBinder<RoomSearchResult>? = null
    override val itemDecoration = LinearItemDecoration(2)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = RoomSearchBinder(requireActivity())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun fillDataObject(json: JSONObject): RoomSearchResult {
        val res = RoomSearchResult()
        try {
            //if (!json.has("status") || json.get("status") !=  "running")
            //res.active = false;
            res.name = json.getString("name")
            res.imageUrl = json.getString(APIUrl.SEARCH_USER_IMAGE_URL)
            res.speakers = json.getString("nbActors") //todo: Why?
            //res.viewers = json.getJSONArray("waitList").length() //not anymore
            var tagString = "";
            val tags = json.getJSONArray("tags")
            for (i in 0 until tags.length()) {
                tagString += (tags[i]);
                tagString += " ";
            }
            tagString = tagString.trim();
            res.tags = tagString;
            res.token = json.getString("token")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return res
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

}

class DiscoverRoomRecycler : SearchRoomRecycler() {
    override val layoutObjectId: Int = R.layout.info_discover_room

    override fun setLayoutManager(): RecyclerView.LayoutManager =
            GridLayoutManager(activity, resources.getInteger(R.integer.homepage_rooms_row_number))
}