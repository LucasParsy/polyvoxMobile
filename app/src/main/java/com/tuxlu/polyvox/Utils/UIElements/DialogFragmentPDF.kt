package com.tuxlu.polyvox.Utils.UIElements

/**
 * Created by tuxlu on 06/01/18.
 */

import android.os.Bundle
import android.os.Environment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response

import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.NetworkLibraries.VHttp
import com.tuxlu.polyvox.Utils.NetworkLibraries.VolleyFileDownloader
import com.tuxlu.polyvox.Utils.ToastType
import com.tuxlu.polyvox.Utils.UtilsTemp
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import java.io.File
import es.voghdev.pdfviewpager.library.PDFViewPager




public class DialogFragmentPDF() : DialogFragmentBase()   {

    private lateinit var layout: View;
    private lateinit var pdfViewPager: PDFViewPager;
    private var keepFile : Boolean = false
    private var file : File? = null

    public override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        layout = setupLayout(inflater, container, R.layout.util_file_pdf)
        val downButt = layout.findViewById<ImageButton>(R.id.download)
        downButt.setOnClickListener(View.OnClickListener { _ ->
            UtilsTemp.showToast(activity, name + getString(R.string.downloaded))
            keepFile = true
        })

        val body = this.getString(R.string.discovered) + name + "\n"
        layout.findViewById<ImageButton>(R.id.share).setOnClickListener({ _ -> UtilsTemp.shareContent(activity, body, url!!) })
        setFavorite(url, name)
        name = ""
        downloadPDF(url!!)
        return layout
    }

    private fun downloadPDF(url: String) {
        val req = VolleyFileDownloader(Request.Method.GET, url, Response.Listener { response ->
            name = response.first

            val path = File(Environment.getExternalStorageDirectory().toString() + "/Download/Polyvox/")
            file = File(path, name)
            pdfViewPager = PDFViewPager(this.activity, file!!.absolutePath)
            layout.findViewById<ViewPager>(R.id.pager).adapter = pdfViewPager.adapter
            LoadingUtils.EndLoadingView(layout)
        }, Response.ErrorListener { _ ->
            UtilsTemp.showToast(activity, getString(R.string.download_error), ToastType.ERROR)
            LoadingUtils.EndLoadingView(layout)
        })
        req.retryPolicy = DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        VHttp.getInstance(this.activity).addToRequestQueue(req)
    }

    override fun onStop() {
        super.onStop()
        if (pdfViewPager != null)
            (pdfViewPager.adapter as PDFPagerAdapter).close()
        if (!keepFile && file != null)
            file!!.delete()
    }
}