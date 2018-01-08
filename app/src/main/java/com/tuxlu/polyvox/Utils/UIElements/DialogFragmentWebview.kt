package com.tuxlu.polyvox.Utils.UIElements

/**
 * Created by tuxlu on 06/01/18.
 */

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageButton

import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.UtilsTemp

class DialogFragmentWebview() : DialogFragmentBase() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)


        val layout = setupLayout(inflater, container, R.layout.util_file_webview)
        val downButt = layout.findViewById<ImageButton>(R.id.download)
        downButt.visibility = View.GONE

        val body = this.getString(R.string.discovered) + name + "\n"
        layout.findViewById<ImageButton>(R.id.share).setOnClickListener({ _ -> UtilsTemp.shareContent(activity, body, url!!) })
        setFavorite(url, name)
        val webView = layout.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        var nUrl = url;
        webView.loadUrl(nUrl)
        return layout
    }
}