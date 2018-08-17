package com.tuxlu.polyvox.Room

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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.Recyclers.Adapter
import com.tuxlu.polyvox.Utils.Recyclers.IRecycler
import com.tuxlu.polyvox.Utils.Recyclers.ViewHolderBinder
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import com.tuxlu.polyvox.Utils.UtilsTemp
import org.json.JSONObject
import java.util.*


/**
 * Created by tuxlu on 12/11/17.
 */

enum class RoomWaitlistStatus {
    NONE, STREAMER, MOVEUP, WAIT
}


data class RoomWaitlistResult(var username: String = "",
                              var url: String = "",
                              var time: Int = 0,
                              var status: RoomWaitlistStatus = RoomWaitlistStatus.NONE,
                              var timer: CountDownTimer? = null)


open class RoomWaitlistBinder : ViewHolderBinder<RoomWaitlistResult> {

    var timeLimit: Int = 0
    private lateinit var timeText: TextView

    private fun secondToText(nSeconds: Int, isLenght: Boolean = false): String {
        var seconds = nSeconds
        if (!isLenght)
            seconds = timeLimit - nSeconds
        return (String.format("%02d:%02d", seconds / 60, seconds % 60))
    }

    override fun bind(holder: Adapter.ViewHolder<RoomWaitlistResult>, item: RoomWaitlistResult) {
        //Log.wtf("WAITLISTBIND", "binded " + item.username + " " + item.status)
        holder.v.findViewById<TextView>(R.id.infoUserName).text = item.username
        val image = holder.v.findViewById<ImageView>(R.id.infoUserPicture)
        if (!UtilsTemp.isStringEmpty(item.url))
            GlideApp.with(holder.v.context).load(item.url).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_account_circle_black_24dp).into(image)
        else {
            GlideApp.with(holder.v.context).clear(image)
        }
        val statIcon = holder.v.findViewById<ImageView>(R.id.statusIcon)

        holder.v.findViewById<TextView>(R.id.timePassed).visibility = View.INVISIBLE
        holder.v.findViewById<TextView>(R.id.timeLimit).visibility = View.INVISIBLE


        if (item.status == RoomWaitlistStatus.NONE)
            statIcon.visibility = View.INVISIBLE
        else
            statIcon.visibility = View.VISIBLE

        if (item.status == RoomWaitlistStatus.MOVEUP)
            statIcon.setImageResource(R.drawable.arrow_up)
        if (item.status == RoomWaitlistStatus.WAIT)
            statIcon.setImageResource(R.drawable.wait_clock)

        if (item.status == RoomWaitlistStatus.STREAMER) {
            statIcon.setImageResource(R.drawable.speaker)
            holder.v.findViewById<TextView>(R.id.timePassed).visibility = View.VISIBLE
            if (timeLimit > 0) {
                holder.v.findViewById<TextView>(R.id.timeLimit).visibility = View.VISIBLE
                holder.v.findViewById<TextView>(R.id.timeLimit).text = "/" + secondToText(timeLimit, true)
            }
            timeText = holder.v.findViewById<TextView>(R.id.timePassed)
            timeText.text = secondToText(item.time)

            if (item.timer != null) {
                item.timer!!.onFinish()
                item.timer!!.cancel()
            }
            item.timer = null

            /*
            item.timer = object : CountDownTimer(timeLimit.toLong() * 1000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    timeText.text = secondToText(item.time)
                    item.time--
                }

                override fun onFinish() {
                    //dunno if it will be used
                }
            }
            item.timer!!.start()
        */
        } else {
            if (item.timer != null)
                item.timer!!.onFinish()
            holder.v.findViewById<TextView>(R.id.timePassed).visibility = View.INVISIBLE
            holder.v.findViewById<TextView>(R.id.timeLimit).visibility = View.INVISIBLE
        }
    }

    override fun setClickListener(holder: Adapter.ViewHolder<RoomWaitlistResult>, data: MutableList<RoomWaitlistResult>) {
        val rootView = holder.v.findViewById<View>(R.id.infoRoomLayout)
        if (rootView.hasOnClickListeners())
            return
        //rootView.setOnClickListener { 1+1 }
        //i dunno, what can you do here?
    }
}

class RoomWaitlistAdapter(nContext: Context,
                          nData: MutableList<RoomWaitlistResult>,
                          nLayout: Int,
                          nBinder: ViewHolderBinder<RoomWaitlistResult>,
                          private val recycler: RecyclerView) : Adapter<RoomWaitlistResult>(nContext, nData, nLayout, nBinder) {

    private fun findUser(name: String): Int {
        for (i in 0 until data.size)
            if (data[i].username == name)
                return i
        return -1
    }

    fun deleteUser(name: String) {
        val pos = findUser(name)
        if (pos == -1)
            return

        data.removeAt(pos)
        recycler.removeViewAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, data.size)
        if (pos < data.size)
            if (pos == 0)
                data[pos].status = RoomWaitlistStatus.STREAMER
            else
                data[pos].status = RoomWaitlistStatus.MOVEUP
        notifyItemChanged(pos)
    }

    fun addUser(nData: RoomWaitlistResult) {
        data.add(nData)
        notifyItemInserted(data.size - 1)
    }

    fun waitUser(name: String) {
        val pos = findUser(name)
        if (pos == -1)
            return
        data[pos].status = RoomWaitlistStatus.WAIT
        if (pos + 1 < data.size - 1) {
            //todo: c'est la merde si le dernier membre de la liste souhaite attendre.
            //sinon, n'appeler cette fonction que quand la rotation est déja effectuée,
            //comme ça il devient avant dernier.

            Collections.swap(data, pos, pos + 1)
            notifyItemMoved(pos, pos + 1)
            notifyItemChanged(pos + 1)
        } else
            notifyItemChanged(pos)
    }

    fun moveDownSpeaker() {
        if (data.size < 2)
            return
        data[0].status = RoomWaitlistStatus.NONE
        notifyItemChanged(0)
        Collections.rotate(data, -1)
        notifyItemMoved(0, data.size - 1)
        data[0].status = RoomWaitlistStatus.STREAMER
        notifyItemChanged(0)
        recycler.scrollToPosition(0)

        //reset status for all other items
        for (i in 1 until data.size)
            if (data[i].status != RoomWaitlistStatus.NONE &&
                    data[i].status != RoomWaitlistStatus.STREAMER) {
                data[i].status = RoomWaitlistStatus.NONE
                notifyItemChanged(i)
            }
    }

    fun startTimer(position: Int) {
        if (position < data.size && data[position].timer != null)
            data[position].timer!!.start()
    }

    fun stopTimer(position: Int) {
        if (position < data.size && data[position].timer != null)
            data[position].timer!!.cancel()
    }

    fun deleteTimers() {
        for (item in data) {
            if (item.timer != null) {
                item.timer!!.cancel()
                item.timer = null
            }
        }
    }
}


class RoomWaitlistRecycler : IRecycler<RoomWaitlistResult>() {

    override val layoutListId: Int = R.layout.fragment_recycler_view
    override val layoutObjectId: Int = R.layout.info_waitlist
    override val recycleId: Int = R.id.recycleView
    override val requestObjectName: String = APIUrl.SEARCH_USER_JSONOBJECT

    override lateinit var binder: RoomWaitlistBinder
    override val itemDecoration = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = RoomWaitlistBinder()

        val res = super.onCreateView(inflater, container, savedInstanceState)

        val recycler = rootView.findViewById<RecyclerView>(recycleId)
        adapter = RoomWaitlistAdapter(context!!, ArrayList(), layoutObjectId, binder, recycler)
        recycler.adapter = adapter

        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 0
        itemAnimator.removeDuration = 1000
        itemAnimator.moveDuration = 1000
        itemAnimator.changeDuration = 500
        recycler.itemAnimator = itemAnimator
        LoadingUtils.EndLoadingView(rootView)
        return res
    }

    fun setTimeLimit(nLimit: Int) {
        binder.timeLimit = nLimit
    }

    fun update(data: List<RoomWaitlistResult>, clear: Boolean = false) {
        if (clear)
            adapter?.clear(false)
        adapter?.add(data)
        adapter?.notifyDataSetChanged()
    }

    fun moveDownSpeaker() = (adapter as RoomWaitlistAdapter?)?.moveDownSpeaker()

    private fun startTimer(position: Int) = (adapter as RoomWaitlistAdapter?)?.startTimer(position)

    private fun stopTimer(position: Int) = (adapter as RoomWaitlistAdapter?)?.stopTimer(position)

    fun addUser(user: RoomWaitlistResult) = (adapter as RoomWaitlistAdapter?)?.addUser(user)

    fun deleteUser(name: String) = (adapter as RoomWaitlistAdapter?)?.deleteUser(name)

    fun waitUser(name: String) = (adapter as RoomWaitlistAdapter?)?.waitUser(name)


    override fun fillDataObject(json: JSONObject): RoomWaitlistResult {
        return RoomWaitlistResult("stub", "not needed")
    }

    override fun setLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

    override fun onDestroy() {
        super.onDestroy()
        (adapter as RoomWaitlistAdapter?)?.deleteTimers()
    }
}
