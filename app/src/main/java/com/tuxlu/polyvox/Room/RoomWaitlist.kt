package com.tuxlu.polyvox.Room

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tuxlu.polyvox.R
import kotlinx.android.synthetic.main.fragment_room_waitlist.*


/**
 * Created by tuxlu on 17/01/18.
 */
class RoomWaitlist : Fragment() {

    lateinit var rootView: View
    private lateinit var frag: RoomWaitlistRecycler

    private var handler: Handler = Handler()
    private val runnable: Runnable = Runnable { YoYo.with(Techniques.SlideOutDown).duration(300).playOn(notificationText) };

    private var hanDummyList: Handler = Handler()


    fun showInfo(text: String) {
        notificationText.text = text;
        notificationText.visibility = View.VISIBLE
        YoYo.with(Techniques.SlideInUp).duration(300).playOn(notificationText)
        handler.removeCallbacks(null)
        handler.postDelayed(runnable, 10000) //10 seconds
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_room_waitlist, container, false)


        val timeLimit = arguments?.getInt("timeLimit")
        val bundle = Bundle()
        bundle.putInt("timeLimit", timeLimit!!)
        frag = RoomWaitlistRecycler()
        frag.arguments = bundle
        fragmentManager!!.beginTransaction().add(R.id.roomWaitlistLayout, frag).commit()

        rootView.findViewById<TextView>(R.id.notificationText).setOnClickListener { onNotificationClick() }
        startDummyWaitlist()

        return rootView;
    }

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

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
        hanDummyList.removeCallbacksAndMessages(null)
    }

    override fun onStart() {
        super.onStart()
        startDummyHandlers()
    }

    public fun onNotificationClick()
    {
        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(notificationText)
        handler.removeCallbacksAndMessages(null)
    }

}