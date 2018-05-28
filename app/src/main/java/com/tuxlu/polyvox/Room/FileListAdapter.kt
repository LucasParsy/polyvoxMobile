package com.tuxlu.polyvox.Room

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.Fileloader
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp


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
        if (group.url.isBlank())
            v.findViewById<View>(R.id.view2).visibility = View.INVISIBLE
        else
            GlideApp.with(v.context).load(group.url).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_account_circle_black_24dp).into(image)
    }

    fun rotateIcon(from: Float) {
        val arrow  = v.findViewById<ImageView>(R.id.fileListArrow)
        arrow.animate().rotation(from)
    }

    override fun collapse() {
        super.collapse()
        rotateIcon(0f)
    }

    override fun expand() {
        super.expand()
        rotateIcon(90f)
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