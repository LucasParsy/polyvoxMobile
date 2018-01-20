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

data class FlFile(var name: String = "",
                  var url: String = "",
                  var size: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeInt(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FlFile> {
        override fun createFromParcel(parcel: Parcel): FlFile {
            return FlFile(parcel)
        }

        override fun newArray(size: Int): Array<FlFile?> {
            return arrayOfNulls(size)
        }
    }
}

data class FlUser(var username: String = "",
                  var url: String = "",
                  private var files: List<FlFile>) : ExpandableGroup<FlFile>(username, files)


open class FlUserBinder(val v: View) : GroupViewHolder(v) {

    fun setData(group: FlUser) {
        v.findViewById<TextView>(R.id.infoUserName).text = group.username
        val image = v.findViewById<ImageView>(R.id.infoUserPicture)
        GlideApp.with(v.context).load(group.url).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_account_circle_black_24dp).into(image)
    }

    fun rotateIcon(from: Float, to: Float) {
        val rotate = RotateAnimation(from, to, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        v.findViewById<ImageView>(R.id.fileListArrow).animation = rotate
    }

    override fun collapse() {
        super.collapse()
        rotateIcon(180f, 360f)
    }

    override fun expand() {
        super.expand()
        rotateIcon(360f, 180f)
    }
}

open class FlFileBinder(val v: View) : ChildViewHolder(v) {

    fun setData(data: FlFile) {
        v.findViewById<TextView>(R.id.ckDocumentName).text = data.name
        v.findViewById<TextView>(R.id.ckDocumentSize).text = data.size.toString()
        val image = v.findViewById<ImageView>(R.id.documentImage)
        val type = Fileloader.getType("", data.url)
        if (type.startsWith("image"))
            GlideApp.with(v.context).load(data.url).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_account_circle_black_24dp).into(image)
        else if (type == "application/pdf")
            image.setImageResource(R.drawable.flaticon_pdf_dummy)
        else
            image.setImageResource(R.drawable.document)
    }
}


class FilelistAdapter(groups: List<FlUser>, private val act: Activity) : ExpandableRecyclerViewAdapter<FlUserBinder, FlFileBinder>(groups) {

    var data = groups;

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): FlUserBinder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.info_filelist_user, parent, false)
        return FlUserBinder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): FlFileBinder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.info_filelist_file, parent, false)
        return FlFileBinder(view)
    }

    override fun onBindChildViewHolder(holder: FlFileBinder, flatPosition: Int, group: ExpandableGroup<*>,
                                       childIndex: Int) {
        val data = (group as FlUser).items[childIndex];
        holder.setData(data)
        holder.v.findViewById<View>(R.id.rootView).setOnClickListener {
            Fileloader.openFile(data.name, data.url, "", act);
        }
    }

    override fun onBindGroupViewHolder(holder: FlUserBinder, flatPosition: Int,
                                       group: ExpandableGroup<*>) {
        holder.setData(group as FlUser)
    }
}