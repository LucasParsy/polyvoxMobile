package com.tuxlu.polyvox.Utils

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import com.tuxlu.polyvox.R
import android.webkit.WebResourceRequest
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_search.*
import android.widget.ProgressBar




/**
 * Created by tuxlu on 24/11/17.
 */


class SimpleWebView : AppCompatActivity() {
    lateinit var rootView : View

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.simple_webview)
        //todo: view could break on some devices, see https://stackoverflow.com/questions/4486034/get-root-view-from-current-activity
        rootView = findViewById(android.R.id.content)
        val title = intent.getIntExtra("title", -1);
        val url = intent.getStringExtra("url");

        setTitle(title)
        val webView = this.findViewById<WebView>(R.id.webView)
        webView.settings.setJavaScriptEnabled(true)
        webView.settings.setDomStorageEnabled(true)
        webView.loadUrl(url)
        webView.webViewClient = AppWebViewClients()
        LoadingUtils.StartLoadingView(rootView, applicationContext);
    }


    inner class AppWebViewClients() : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            LoadingUtils.EndLoadingView(rootView);
            LoadingUtils.endNoWifiView(rootView);
        }
    }

}