package com.tuxlu.polyvox.Utils.UIElements

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tuxlu.polyvox.R


/**
 * Created by tuxlu on 24/11/17.
 */


class SimpleWebView : AppCompatActivity() {
    lateinit var rootView : View

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.util_webview)
        //todo: view could break on some devices, see https://stackoverflow.com/questions/4486034/get-root-view-from-current-activity
        rootView = findViewById(android.R.id.content)
        val title = intent.getIntExtra("title", -1)
        val url = intent.getStringExtra("url")

        setTitle(title)
        val webView = this.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.loadUrl(url)
        webView.webViewClient = AppWebViewClients()
        LoadingUtils.StartLoadingView(rootView, applicationContext)
    }


    inner class AppWebViewClients : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            LoadingUtils.EndLoadingView(rootView)
            LoadingUtils.endNoWifiView(rootView)
        }
    }

}