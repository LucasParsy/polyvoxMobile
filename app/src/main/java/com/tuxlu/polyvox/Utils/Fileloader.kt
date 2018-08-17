package com.tuxlu.polyvox.Utils

import android.Manifest
import android.app.*
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.widget.ImageButton
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.NetworkLibraries.VHttp
import com.tuxlu.polyvox.Utils.NetworkLibraries.VolleyFileDownloader
import com.tuxlu.polyvox.Utils.UIElements.DialogFragmentImage
import com.tuxlu.polyvox.Utils.UIElements.DialogFragmentPDF
import com.tuxlu.polyvox.Utils.UIElements.DialogFragmentWebview
import java.io.File
import java.net.URLConnection


/**
 * Created by tuxlu on 01/01/18.
 */


class Fileloader {

    companion object {

        @JvmStatic
        fun getUrlLastSegment(url: String): String {
            var name = ""
            name = url.trimEnd('/')
            val start: Int = name.lastIndexOf("/") + 1
            name = name.substring(start)
            return name
        }

        @JvmStatic
        fun openFile(nName: String, url: String, ntype: String = "", act: Activity) {
            var name = nName
            if (name.isBlank())
                name = getUrlLastSegment(url)
            var type = getType(ntype, url)

            var newFragment: Fragment? = null
            newFragment = if (type.startsWith("image"))
                DialogFragmentImage()
            else if (type == "application/pdf" && Build.VERSION.SDK_INT >= 21)
                DialogFragmentPDF()
            else if (type == "text/html")
                DialogFragmentWebview()
            else
                return downloadFile(url, act)
            startFragment(name, url, act, newFragment)
        }

        @JvmStatic
        fun getType(ntype: String, url: String): String {
            if (!ntype.isBlank())
                return ntype
            var type = ""
            type = URLConnection.guessContentTypeFromName(url)
            val filename: Int = url.lastIndexOf("/")
            if (url.indexOf('.', filename) == -1)
                type = "text/html"
            return type
        }

        @JvmStatic
        private fun setFavorite(url: String, name: String, context: Context, view: Dialog) {
            val fav = view.findViewById<ImageButton>(R.id.favorite)
            fav.setOnClickListener({ _ ->
                /*todo: set favorite/unfavorite of link, if we create this functionality
                check if already favorite, and then change hearth icon and send API call when clicking button,
                long thing to do, and we don't know if we will do it one day...
                */
            })
        }


        @JvmStatic
        private fun downloadFile(url: String, context: Activity) {

            if (!UtilsTemp.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                return
            val req = VolleyFileDownloader(Request.Method.GET, url, Response.Listener { response ->
                val file = File(Environment.getExternalStorageDirectory().toString() + "/Download/Polyvox/",
                        response.first)
                val path = Uri.fromFile(file)
                var uri: Uri = FileProvider.getUriForFile(context, "com.tuxlu.fileprovider", file)
                val intent = Intent()
                //val clip : ClipData = ClipData.newRawUri(response.first, path)
                intent.action = Intent.ACTION_VIEW
                //intent.data = path
                //intent.type = response.second
                intent.setDataAndType(uri, response.second)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    UtilsTemp.showToast(context, response.first + context.getString(R.string.downloaded))
                }

            }, Response.ErrorListener { _ ->
                UtilsTemp.showToast(context, context.getString(R.string.download_error), ToastType.ERROR)
            })
            req.retryPolicy = DefaultRetryPolicy(15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            VHttp.getInstance(context.applicationContext).addToRequestQueue(req)
        }

        @JvmStatic
        private fun startFragment(name: String, url: String, act: Activity, frag: Fragment) {
            val fragmentManager: FragmentManager = act.fragmentManager
            val bundle = Bundle()
            bundle.putString("url", url)
            bundle.putString("name", name)
            frag.arguments = bundle
            val transaction = fragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.add(android.R.id.content, frag).addToBackStack(null).commit()
        }

    }
}