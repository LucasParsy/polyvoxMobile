package com.tuxlu.polyvox.Room

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.View
import com.android.volley.Request
import com.google.android.gms.ads.InterstitialAd
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.exo_stream_playback_control.*


/**
 * Created by tuxlu on 16/09/17.
 */


class RoomDirect : RoomBase(), DialogFragmentInterface {
    private var manifestHandler: Handler = Handler()
    private val manifestRunnable: Runnable = Runnable { getManifest() }

    lateinit var userRate: UserRating
    private lateinit var waitlist: Waitlist

    private var firstManifest = true

    override fun onCreate(savedInstanceState: Bundle?) {
        mInterstitialAd = InterstitialAd(this)
        super.onCreate(savedInstanceState)
        userRate = UserRating(this, token)

        val bundle = Bundle()
        bundle.putString("token", token)
        waitlist = Fragment.instantiate(this, Waitlist::class.java.name) as Waitlist
        waitlist.arguments = bundle

        val username = AuthUtils.getUsername(applicationContext)
        val fragments = getCommonFragments(username)
        fragments.add(waitlist)
        setUpFragments(fragments)

        manifestHandler.post(manifestRunnable)
    }

    private fun getManifest() {
        APIRequest.JSONrequest(baseContext, Request.Method.GET,
                APIUrl.BASE_URL + APIUrl.ROOM + token + APIUrl.ROOM_MANIFEST, false, null, { response ->
            val data = response.getJSONObject("data")
            try {
                waitlist.update(data)
                fileList.add(data, true)
                if (firstManifest) {
                    firstManifest = false
                    if (UtilsTemp.isStringEmpty(data.getString("speaker"))) {
                        loadingRoomProgress.visibility = View.GONE
                        bufferingProgress.visibility = View.INVISIBLE
                        manifestHandler.postDelayed({ waitingText.visibility = View.VISIBLE }, 1000)
                    }
                }
            } catch (e: Exception) {
            }


            //userRate.showUserRating("tuxlu42", "https://polyvox.fr/public/img/tuxlu42.png");


            val nUrl = data.getString("videosData")
            streaming.setVideoPlayer(token, nUrl, this)
            manifestHandler.postDelayed(manifestRunnable, 2000)
        },
                {
                    manifestHandler.postDelayed(manifestRunnable, 2000)
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        manifestHandler.removeCallbacksAndMessages(null)
    }

    override fun dialogDismiss() {
        userRate.closeUserRating(reportButton)
    }
}
