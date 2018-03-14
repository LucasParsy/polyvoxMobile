package com.tuxlu.polyvox.Search

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.User.ProfilePage
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.IRecycler
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import com.tuxlu.polyvox.Utils.UtilsTemp
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
        val image = holder.v.findViewById<ImageView>(R.id.infoUserPicture)
        if (!UtilsTemp.isStringEmpty(item.imageUrl))
            GlideApp.with(holder.v.context).load(item.imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_account_circle_black_24dp).into(image)
    }

    override fun setClickListener(holder: Adapter.ViewHolder<UserSearchResult>, data: MutableList<UserSearchResult>)
    {
        val context = holder.v.context
        val clickListener = View.OnClickListener {_ ->

            val intent = Intent(context, ProfilePage::class.java)
            val b = Bundle()
            b.putString("name", data[holder.adapterPosition].name)
            intent.putExtras(b)
            context.startActivity(intent)
        }
        holder.v.findViewById<View>(R.id.infoRoomLayout).setOnClickListener(clickListener)
    }
}

class LinearItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State?) {
        outRect.top = spaceHeight
    }
}



class SearchUserRecycler : IRecycler<UserSearchResult>() {

    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val layoutObjectId: Int = R.layout.info_search_user
    override val recycleId: Int = R.id.recycleView
    override val requestObjectName : String = APIUrl.SEARCH_USER_JSONOBJECT

    override val binder = UserSearchBinder()
    override val itemDecoration= LinearItemDecoration(2)



    override fun fillDataObject(json: JSONObject): UserSearchResult
    {
        val res = UserSearchResult()
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
