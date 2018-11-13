package com.tuxlu.polyvox.Utils.UIElements

/**
 * Created by tuxlu on 06/01/18.
 */

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageButton

import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.UtilsTemp
import org.w3c.dom.Text

class DialogFragmentDemoChangeURL : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)


        val layout = inflater.inflate(R.layout.info_util_demo, container, false)
        val butt = layout.findViewById<Button>(R.id.button)
        val text = layout.findViewById<TextInputEditText>(R.id.input)
        butt.setOnClickListener {
            if (!text.text.isNullOrBlank()) {
                APIUrl.BASE_URL = "http://" + text.text.toString() + ":8000/"
                APIUrl.CHAT_URL = "ws://" + text.text.toString() + ":8042/"
            }
            layout.visibility = View.GONE
        }
        return layout
    }
}