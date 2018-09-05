package com.tuxlu.polyvox.Room

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.Request
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import kotlinx.android.synthetic.main.fragment_room_waitlist.*
import org.json.JSONArray
import org.json.JSONObject


/**
 * Created by tuxlu on 17/01/18.
 */
class Waitlist : Fragment() {

    lateinit var rootView: View
    private lateinit var frag: RoomWaitlistRecycler

    private var handler: Handler = Handler()
    private val runnable: Runnable = Runnable { YoYo.with(Techniques.SlideOutDown).duration(300).playOn(notificationText) }

    private var oldJSon = ""
    private var token = ""
    private var currentSpeaker: RoomWaitlistResult? = null
    private var nextSpeaker = ""
    private var waiters = ArrayList<String>()

    private var firstUpdate = true

    private fun showInfo(text: String) {
        if (firstUpdate)
            return
        notificationText.text = text
        notificationText.visibility = View.VISIBLE
        YoYo.with(Techniques.SlideInUp).duration(300).playOn(notificationText)
        handler.removeCallbacks(null)
        handler.postDelayed(runnable, 10000) //10 seconds
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_room_waitlist, container, false)


        token = arguments?.getString("token")!!
        frag = RoomWaitlistRecycler()
        fragmentManager!!.beginTransaction().add(R.id.roomWaitlistLayout, frag).commit()

        rootView.findViewById<TextView>(R.id.notificationText).setOnClickListener { onNotificationClick() }
        settimeLimit()
        return rootView
    }

    fun settimeLimit() {
        APIRequest.JSONrequest(context, Request.Method.GET,
                APIUrl.BASE_URL + APIUrl.ROOM + token, false, null,
                { response ->
                    val dataName = APIUrl.SEARCH_USER_JSONOBJECT
                    val speakerDurname = "speakerMaxDuration"

                    if (response.has(dataName)) {
                        val data = response.getJSONObject(dataName)
                        if (data.has(speakerDurname)) {
                            val duration = data.getInt(speakerDurname)
                            frag.setTimeLimit(duration)
                        }
                    }
                }, null)
    }

    private fun showRating(newSpeaker: RoomWaitlistResult?) {
        if (activity is RoomDirect) {
            (activity as RoomDirect).showAd()
            (activity as RoomDirect).userRate.showUserRating(currentSpeaker!!.username, currentSpeaker!!.url)
        }
        currentSpeaker = newSpeaker
    }

    fun update(data: JSONObject) {
        val list = ArrayList<RoomWaitlistResult>()

        if (data.isNull("speaker")) {
            if (currentSpeaker != null)
                showRating(null)
        } else {
            val jsonSpeaker = data.getJSONObject("speaker")
            val speakerTime = jsonSpeaker.getInt("countdownValue")
            val jsonSpeakerInfo = jsonSpeaker.getJSONObject("info")

            val speakerInfo = RoomWaitlistResult(jsonSpeakerInfo.getString("userName"),
                    jsonSpeakerInfo.getString("picture"), speakerTime, RoomWaitlistStatus.STREAMER)

            if (currentSpeaker != null) {
                if (speakerInfo.username != currentSpeaker!!.username)
                    showRating(speakerInfo)
            }
            currentSpeaker = speakerInfo
            list.add(speakerInfo)
        }

        val waitlist = data.getJSONArray("waitList")
        for (i in 0 until waitlist.length()) {
            val name = waitlist.getJSONObject(i).getString("userName")
            val url = waitlist.getJSONObject(i).getString("picture")
            list.add(RoomWaitlistResult(name, url))
        }
        frag.update(list, true)
        /*
        val jStr = waitlist.toString()
        if (oldJSon == jStr)
            return;
        oldJSon = jStr

        checkNewUsers(waitlist, speakerInfo)
        //checkDeleteUsers(waitlist, speakerInfo)
        //checkWaitUser(waitlist, speakerInfo)

        if (nextSpeaker == speakerInfo.username) {
            frag.moveDownSpeaker()
            currentSpeaker = speakerInfo.username;
            nextSpeaker = ""
            if (waitlist.length() > 0)
                nextSpeaker = waitlist.getJSONObject(0).getString("userName")
        }

        firstUpdate = false;
        /*
            frag.deleteUser(name)
            frag.waitUser(name)
            //always check for the "nextSpeaker" change!
        */
        */
    }

    private fun checkNewUsers(waitlist: JSONArray, speakerInfo: RoomWaitlistResult) {
        val speakerName = speakerInfo.username

        if (speakerName != currentSpeaker?.username && !waiters.contains(speakerName)) {
            currentSpeaker = RoomWaitlistResult(speakerName, speakerInfo.url, speakerInfo.time, speakerInfo.status)
            frag.addUser(speakerInfo)
            waiters.add(speakerName)
            showInfo(speakerName + getString(R.string.joined_waitlist))
        }

        for (i in 0..waitlist.length()) {
            val name = waitlist.getJSONObject(i).getString("userName")
            val url = waitlist.getJSONObject(i).getString("picture")

            if (!waiters.contains(name)) {
                frag.addUser(RoomWaitlistResult(name, url))
                waiters.add(name)
                showInfo(name + getString(R.string.joined_waitlist))

                if (nextSpeaker == "")
                    nextSpeaker = speakerName
            }
        }
    }


    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    fun onNotificationClick() {
        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(notificationText)
        handler.removeCallbacksAndMessages(null)
    }


    /*
    private fun startDummyHandlers() {
        hanDummyList.postDelayed(object : Runnable {
            override fun run() {
                frag.moveDownSpeaker()
                hanDummyList.postDelayed(this, 10000);
            }
        }
                , 10000)


        hanDummyList.postDelayed({
            //frag.waitUser("JMLP")
            //showInfo("JMLP" + getString(R.string.waited_turn))

            frag.waitUser("JLM")
            showInfo("JLM" + getString(R.string.waited_turn))
        }
                , 9000)


        //todo: crashs, but may be unused in the future
        /*
        hanDummyList.postDelayed({
            //frag.waitUser("JMLP")
            //showInfo("JMLP" + getString(R.string.waited_turn))

            frag.deleteUser("JMLP")
            showInfo("JMLP" + getString(R.string.left_waitlist))
        }
                , 23000)
        */

        hanDummyList.postDelayed({
            //frag.waitUser("JMLP")
            //showInfo("JMLP" + getString(R.string.waited_turn))

            frag.addUser(RoomWaitlistResult("MisterMV",
                    "https://yt3.ggpht.com/a-/AK162_5W9yHOX95l9xCYHk1YKskqfF8YYowrTYH3YQ=s900-mo-c-c0xffffffff-rj-k-no"))
            showInfo("MisterMV" + getString(R.string.joined_waitlist))
        }
                , 32000)

    }

    private fun startDummyWaitlist() {
        hanDummyList.postDelayed({
            frag.addUser(RoomWaitlistResult("tuxlu", "https://polyvox.fr/public/img/tuxlu42.png", 0, RoomWaitlistStatus.STREAMER))
            frag.addUser(RoomWaitlistResult("morsay", "https://i.ytimg.com/vi/uoarvMjyKj4/maxresdefault.jpg"))
            frag.addUser(RoomWaitlistResult("JLM", "https://www.cfr.org/content/publications/Melenchon.jpg"))
            frag.addUser(RoomWaitlistResult("dorian", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Dorian_-_Japan_Party_2013_-_P1580289.JPG/150px-Dorian_-_Japan_Party_2013_-_P1580289.JPG"))
            frag.addUser(RoomWaitlistResult("JMLP", "https://www.touteleurope.eu/fileadmin/_TLEv3/candidats/jean_marie_le_pen.jpg"))
        }
                , 1000)


    }
    */
}