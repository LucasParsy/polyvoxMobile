package com.tuxlu.polyvox.Room

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.fragment_user_report.*
import org.json.JSONObject

/**
 * Created by tuxlu on 19/01/18.
 */

open abstract class CommentReportBase : DialogFragment() {
    abstract val layout: Int
    protected var name: String = ""
    protected var token: String = ""
    protected var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = arguments.getString("name")
        url = arguments.getString("url")
        token = arguments.getString("token")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater!!.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        infoUserName.text = name
        if (!UtilsTemp.isStringEmpty(url))
            GlideApp.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).into(infoUserPicture)
        sendButton.setOnClickListener { _: View -> buttonClick() }
    }

    abstract fun buttonClick()
}

class UserCommentFragment : CommentReportBase() {
    override val layout: Int = R.layout.fragment_user_comment

    override fun buttonClick() {
        if (input.text.isNullOrBlank())
            return

        /*
        val reasonText = reasonSpinner.selectedItem.toString()
        val body = JSONObject()
        body.put(APIUrl.UPDATE_INF0_BIO, name)
        body.put(???, reasonText)
        body.put(???, input.text)
        APIRequest.JSONrequest(this, Request.Method.POST,
        APIUrl.BASE_URL + APIUrl.???, true, body, { _ ->
            UtilsTemp.showToast(this, getString(R.string.???))
        }, null)
        */
        if (activity is RoomDirect) {
            val act: RoomDirect = activity as RoomDirect
            act.dialogDismiss()
        }
        dismiss()
    }
}

class UserReportFragment : CommentReportBase() {
    override val layout: Int = R.layout.fragment_user_report

    override fun buttonClick() {
        if (input.text.isNullOrBlank() || reasonSpinner.selectedItemPosition == 0) {
            reportErrorText.visibility = View.VISIBLE
            return
        }


        val reasonText = reasonSpinner.selectedItem.toString()
        val body = JSONObject()
        body.put("userReported", name)
        body.put("motive", reasonText)
        body.put("roomToken", token)
        val context = this.activity.baseContext
        APIRequest.JSONrequest(context, Request.Method.POST,
                APIUrl.BASE_URL + APIUrl.REPORT, false, body, { _ ->
            UtilsTemp.showToast(context, context.getString(R.string.user_reported, name))
        }, null)

        if (activity is RoomDirect) {
            val act: RoomDirect = (activity as RoomDirect)
            act.dialogDismiss()
        }
        dismiss()
    }

}