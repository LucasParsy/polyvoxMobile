package com.tuxlu.polyvox.Utils

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.toolbox.NetworkImageView
import com.tuxlu.polyvox.Homepage.DiscoverBinder
import com.tuxlu.polyvox.Homepage.DiscoverBox
import com.tuxlu.polyvox.Homepage.SpaceItemDecoration
import com.tuxlu.polyvox.R
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by tuxlu on 12/11/17.
 */

data class UserSearchResult(var name: String ="",
                       var imageUrl: String ="")


open class UserSearchBinder : ViewHolderBinder<UserSearchResult> {

    override fun bind(holder: Adapter.ViewHolder<UserSearchResult>, item: UserSearchResult)
    {
        holder.v.findViewById<TextView>(R.id.infoUserName).text = item.name
        var image = holder.v.findViewById<NetworkImageView>(R.id.infoUserPicture)
        var vHttp = VHttp.getInstance(holder.v.context.applicationContext)
        if (!item.imageUrl.isBlank())
            image.setImageUrl(item.imageUrl, vHttp.imageLoader)
        image.setDefaultImageResId(R.drawable.ic_account_circle_black_24dp)
    }

    override fun setClickListener(holder: Adapter.ViewHolder<UserSearchResult>, data: MutableList<UserSearchResult>)
    {
        val context = holder.v.getContext()
        val clickListener = View.OnClickListener {_ ->
            //todo: user page click intent
            /*
            val intent = Intent(context, Room::class.java)
            val name: String = data[holder.adapterPosition].name
            intent.putExtra("name", name)
            context.startActivity(intent)
            */
        }
        holder.v.findViewById<View>(R.id.infoRoomLayout).setOnClickListener(clickListener);
    }
}

class LinearItemDecoration : RecyclerView.ItemDecoration() {

    private val spaceHeight = 2

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State?) {
        outRect.top = spaceHeight
    }
}



class SearchUserRecycler : IRecycler<UserSearchResult>() {

    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val layoutObjectId: Int = R.layout.search_user_info
    override val recycleId: Int = R.id.recycleView
    override val requestObjectName : String = APIUrl.SEARCH_USER_JSONOBJECT

    override val binder = UserSearchBinder()
    override val itemDecoration= LinearItemDecoration()



    override fun fillDataObject(json: JSONObject): UserSearchResult
    {
        var res = UserSearchResult()
        try {
            res.name = json.getString(APIUrl.SEARCH_USER_NAME)
            res.imageUrl = json.getString(APIUrl.SEARCH_USER_IMAGE_URL)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return res
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

}
