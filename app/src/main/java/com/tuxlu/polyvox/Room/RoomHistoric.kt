package com.tuxlu.polyvox.Room

//import com.google.android.exoplayer2.source.dash.DashMediaSource
//import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.View
import com.android.volley.Request
import com.google.android.exoplayer2.Player
import com.google.android.gms.ads.InterstitialAd
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.exo_stream_playback_control.*


/**
 * Created by tuxlu on 16/09/17.
 */


class RoomHistoric : RoomBase() {
    override var hasHistory: Boolean = true
    private var manifestHandler: Handler = Handler()
    private val manifestRunnable: Runnable = Runnable { }

    override val tabTitles = intArrayOf(R.string.tab_historic, R.string.tab_chat, R.string.tab_files)
    override val tabIcons = intArrayOf(R.drawable.ic_mic_black_48dp, R.drawable.ic_forum_black_48dp, R.drawable.document_black)

    private lateinit var historic: HistoricListRecycler

    override fun onCreate(savedInstanceState: Bundle?) {
        mInterstitialAd = InterstitialAd(this)
        super.onCreate(savedInstanceState)
        val bundle = Bundle()
        bundle.putString("token", token)
        historic = Fragment.instantiate(this, HistoricListRecycler::class.java.name) as HistoricListRecycler
        historic.arguments = bundle

        val username = AuthUtils.getUsername(applicationContext)
        val fragments = getCommonFragments(username)
        fragments.add(0, historic) //add at beginning
        setUpFragments(fragments)

        manifestHandler.post(manifestRunnable)
    }

    fun playStream(url: String)
    {
        streaming.setVideoPlayer(token, url,this)
    }

    fun addStreamListener(listener: Player.EventListener)
    {
        streaming.addListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        manifestHandler.removeCallbacksAndMessages(null)
    }
}
