package com.tuxlu.polyvox.Utils.UIElements

/**
 * Created by tuxlu on 06/01/18.
 */

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView

import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.UtilsTemp
import java.io.ByteArrayOutputStream
import java.io.File

class DialogFragmentImage : DialogFragmentBase() {

    lateinit var image: ImageView
    private lateinit var layout: View;


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        layout = setupLayout(inflater, container, R.layout.util_file_image)
        image = layout.findViewById(R.id.image)
        GlideApp.with(this.activity).load(url).placeholder(android.R.drawable.ic_menu_gallery).into(image)

        val downButt = layout.findViewById<ImageButton>(R.id.download)
        downButt.setOnClickListener({ _ ->
            if (image.drawable == null)
                return@setOnClickListener
            val draw: BitmapDrawable = image.drawable as BitmapDrawable
            val bitmap = draw.bitmap
            downloadImage(bitmap, name, this.activity) })

        setShareListener()
        setFavorite(url, name)
        return layout
    }

    private fun downloadImage(image: Bitmap, name: String, context: Context, showToast: Boolean = true): File {
        val file = UtilsTemp.getPath(name)
        val bos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 0, bos)
        file.writeBytes(bos.toByteArray())
        if (showToast)
            UtilsTemp.showToast(context, name + context.getString(R.string.downloaded))
        bos.close()
        return file
    }


    private fun setShareListener()
    {
        val body = getString(R.string.discovered) + name + "\n" + url
        layout.findViewById<ImageButton>(R.id.share).setOnClickListener({ _ ->
            if (image.drawable == null)
                return@setOnClickListener
            val draw: BitmapDrawable = image.drawable as BitmapDrawable
            val bitmap = draw.bitmap
            val file = downloadImage(bitmap, name, this.activity, false)
            UtilsTemp.shareImage(this.activity, body, file)
            file.delete();
        })

    }
}