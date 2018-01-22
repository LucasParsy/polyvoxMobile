package com.tuxlu.polyvox.Chat

import android.content.Context
import android.widget.ImageView
import com.stfalcon.chatkit.commons.ImageLoader
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp


/**
 * Created by tuxlu on 08/01/18.
 */

class ImageLoader(val context: Context): ImageLoader {
    override fun loadImage(imageView: ImageView?, url: String?) {
        GlideApp.with(context).load(url).into(imageView)
        //GlideApp.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView)
    }
}