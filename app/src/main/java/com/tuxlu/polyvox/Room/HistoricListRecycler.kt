package com.tuxlu.polyvox.Room

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.Player
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.IRecycler
import com.tuxlu.polyvox.Utils.Recyclers.IRequestRecycler
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import com.tuxlu.polyvox.Utils.UtilsTemp
import org.json.JSONObject
import java.util.*


/**
 * Created by tuxlu on 12/11/17.
 */


data class HistoricListResult(var id: Int,
                              var username: String,
                              var picture: String,
                              var streamUrl: String = "",
                              var isPlaying: Boolean = true,
                              var holder: Adapter.ViewHolder<HistoricListResult>? = null,
                              var timer: CountDownTimer? = null)


open class HistoricListBinder : ViewHolderBinder<HistoricListResult> {

    var timeLimit: Int = 0
    var token: String = ""
    lateinit var act: RoomHistoric
    var isFirst = true

    private fun secondToText(seconds: Int): String {
        return (String.format("%02d:%02d", seconds / 60, seconds % 60))
    }

    override fun bind(holder: Adapter.ViewHolder<HistoricListResult>, item: HistoricListResult) {
        item.holder = holder
        holder.v.findViewById<TextView>(R.id.infoUserName).text = item.username
        val image = holder.v.findViewById<ImageView>(R.id.infoUserPicture)
        if (!UtilsTemp.isStringEmpty(item.picture))
            GlideApp.with(holder.v.context).load(item.picture).placeholder(R.drawable.ic_account_circle_black_24dp).into(image)
        else {
            GlideApp.with(holder.v.context).clear(image)
        }
        holder.v.findViewById<TextView>(R.id.timeLimit).text = "/" + secondToText(timeLimit)

        val timeText = holder.v.findViewById<TextView>(R.id.timePassed)
        timeText.text = secondToText(0)
        item.timer = object : CountDownTimer(timeLimit.toLong() * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeText.text = secondToText(timeLimit - (millisUntilFinished / 1000).toInt())
            }

            override fun onFinish() {}
        }
        setTimerStatus(false, item)
    }


    override fun setClickListener(holder: Adapter.ViewHolder<HistoricListResult>, data: MutableList<HistoricListResult>) {
        /*
        val rootView = holder.v.findViewById<View>(R.id.infoRoomLayout)
        if (rootView.hasOnClickListeners())
            return
        */
        val context = holder.v.context
        val clickListener = View.OnClickListener {

            val selItem = data[holder.layoutPosition]
            if (selItem.isPlaying)
                return@OnClickListener

            for (item in data)
                if (item.isPlaying)
                    setTimerStatus(false, item)

            setTimerStatus(true, selItem)
            loadStreamUrl(selItem, context, token, act)
        }

        holder.v.findViewById<View>(R.id.infoRoomLayout).setOnClickListener(clickListener)
        if (isFirst) {
            clickListener.onClick(null)
            isFirst = false
        }
    }

    companion object {
        @JvmStatic
        fun playStream(item: HistoricListResult, activ: RoomHistoric) {
            activ.playStream(item.streamUrl)
            //act.showAd()
        }

        @JvmStatic
        fun loadStreamUrl(selItem: HistoricListResult, context: Context, token: String, activ: RoomHistoric) {
            //todo: move this method and playStream elsewhere (util?) as iit is used in Recyclerview directly.
            if (selItem.streamUrl.isEmpty()) {
                APIRequest.JSONrequest(context, Request.Method.GET,
                        APIUrl.BASE_URL + APIUrl.HISTORIC + "/" + token + "/" + selItem.id
                        , true, null, Response.Listener<JSONObject> { response ->
                    selItem.streamUrl = response.getJSONObject("data").getString("videosData");
                    playStream(selItem, activ)
                }, null)
            } else
                playStream(selItem, activ)
        }

        fun setTimerStatus(nstatus: Boolean, item: HistoricListResult) {
            if (nstatus == item.isPlaying)
                return;
            item.isPlaying = nstatus;
            val vis = if (item.isPlaying)
                View.VISIBLE
            else
                View.INVISIBLE

            if (item.holder != null) {
                item.holder!!.v.findViewById<ImageView>(R.id.statusIcon).visibility = vis
                item.holder!!.v.findViewById<TextView>(R.id.timePassed).visibility = vis
                item.holder!!.v.findViewById<TextView>(R.id.timeLimit).visibility = vis
            }
            if (item.isPlaying)
                item.timer!!.start()
            else if (item.timer != null)
                item.timer!!.cancel()
        }

    }
}

class HistoricListAdapter(nContext: Context,
                          nData: MutableList<HistoricListResult>,
                          nLayout: Int,
                          nBinder: ViewHolderBinder<HistoricListResult>,
                          private val recycler: RecyclerView) : Adapter<HistoricListResult>(nContext, nData, nLayout, nBinder) {}


class HistoricListRecycler : IRequestRecycler<HistoricListResult>() {
    override val requestBody: JSONObject = JSONObject()
    lateinit var token: String
    override fun errorListener(error: VolleyError) {}
    override lateinit var requestUrl: String
    override val usesAPI = true
    override val requestObjectName: String = "lstSpeakers"


    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val layoutObjectId: Int = R.layout.info_waitlist
    override val recycleId: Int = R.id.recycleView

    override lateinit var binder: HistoricListBinder
    override val itemDecoration = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        token = arguments!!.getString("token")!!
        requestUrl = APIUrl.HISTORIC + "/" + token + "/"
        binder = HistoricListBinder()
        binder.token = token
        val res = super.onCreateView(inflater, container, savedInstanceState)

        val recycler = rootView.findViewById<RecyclerView>(recycleId)
        adapter = HistoricListAdapter(context!!, ArrayList(), layoutObjectId, binder, recycler)
        recycler.adapter = adapter
        return res
    }

    override fun setupData(data: JSONObject) {
        binder.timeLimit = data.getInt("speakerMaxDuration")
        binder.act = activity as RoomHistoric

        val listener = object : Player.DefaultEventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                if (playbackState == Player.STATE_ENDED) {
                    val actors = adapter!!.getsData()
                    for (i in 0 until actors.size) {
                        if (actors[i].isPlaying) {
                            HistoricListBinder.setTimerStatus(false, actors[i])
                            if (i != actors.size - 1) {
                                HistoricListBinder.setTimerStatus(true, actors[i + 1])
                                HistoricListBinder.loadStreamUrl(actors[i + 1], context!!, token, binder.act)
                                break
                            }
                        }
                    }
                }
            }
        }
        binder.act.addStreamListener(listener)
    }

    override fun fillDataObject(json: JSONObject): HistoricListResult {
        val id = json.getInt("id")
        val username = json.getString("userName")
        val picture = json.getString("picture")
        return HistoricListResult(id, username, picture)
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)
}
