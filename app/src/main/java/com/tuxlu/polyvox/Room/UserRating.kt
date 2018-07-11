package com.tuxlu.polyvox.Room

import android.app.Activity
import android.app.FragmentManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.android.volley.Request
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.R.id.closeButton
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.exo_stream_playback_control.*
import org.json.JSONObject

data class UserInfo(val name: String, val url: String)

interface DialogFragmentInterface {
    // you can define any parameter as per your requirement
    fun dialogDismiss()
    }


class UserRating(private val act: Activity, private val token: String) {
    enum class RatingType { NONE, POSITIVE, NEGATIVE }

    private var currentUser =  UserInfo("", "")
    private var ratingValue = RatingType.NONE


    init
    {
        act.closeButton.setOnClickListener({ v -> closeUserRating(v) })
        act.reportButton.setOnClickListener({ v -> reportUser(v) })
        act.commentButton.setOnClickListener({ v -> commentUserRating(v) })

        act.votePositiveButton.setOnClickListener { _ ->
            commonRatingListener(act.votePositiveButton, act.voteNegativeButton, RatingType.POSITIVE, R.color.cornflower_blue_two)
        }
        act.voteNegativeButton.setOnClickListener { _ ->
            commonRatingListener(act.voteNegativeButton, act.votePositiveButton, RatingType.NEGATIVE, android.R.color.holo_red_light)
        }
    }

    public fun showUserRating(user: String, imageUrl: String) {
        val nUser = UserInfo(user, imageUrl)
        if (currentUser.name.isBlank()) {
            currentUser = nUser
            //return //todo: remove comment, it's debug
        }

        act.videoPlayerView!!.controllerHideOnTouch = false
        act.videoPlayerView!!.controllerShowTimeoutMs = 500000000;
        act.videoPlayerView!!.showController()
        act.player_bottom_buttons_bar.visibility = View.GONE
        act.player_bottom_rate_speaker.visibility = View.VISIBLE
        act.commentBarLayout.visibility = View.GONE
        act.ratingBarLayout.visibility = View.VISIBLE
        //player_bottom_rate_speaker.visibility = View.VISIBLE
        YoYo.with(Techniques.SlideInUp).duration(300).playOn(act.player_bottom_rate_speaker)
        act.infoUserName.text = currentUser.name
        if (!UtilsTemp.isStringEmpty(currentUser.url))
            GlideApp.with(act).load(currentUser.url).diskCacheStrategy(DiskCacheStrategy.NONE).into(act.infoUserPicture)
        currentUser = nUser
    }


    private fun commonRatingListener(button: ImageView, invertButton: ImageView, type: RatingType, color: Int) {
        if (ratingValue != type) {
            ratingValue = type
            button.setColorFilter(act.resources.getColor(color))
            invertButton.setColorFilter(act.resources.getColor(android.R.color.darker_gray))
        } else {
            ratingValue = RatingType.NONE
            button.setColorFilter(act.resources.getColor(android.R.color.darker_gray))
        }
        if (ratingValue != RatingType.NONE)
            act.closeButton!!.setImageResource(R.drawable.checkmark_valid)
        else
            act.closeButton!!.setImageResource(R.drawable.grey_cross)
    }


    public fun closeUserRating(v: View) {
        if (ratingValue != RatingType.NONE) {
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

            val url = APIUrl.BASE_URL + APIUrl.ROOM + token + APIUrl.ROOM_VOTE
            val voteValue = if (ratingValue == RatingType.POSITIVE)
                "poceBlo"
            else
                "poceRoge"
            val body = JSONObject()
            body.put("vote", voteValue)
            APIRequest.JSONrequest(act, Request.Method.PUT, url,
                    true, body, { _ -> }, null)


            act.closeButton!!.setImageResource(R.drawable.grey_cross)
            act.ratingBarLayout.visibility = View.GONE
            act.commentBarLayout.visibility = View.VISIBLE
            if (ratingValue == RatingType.POSITIVE)
                act.reportButton.visibility = View.GONE
            else
                act.reportButton.visibility = View.VISIBLE
            //if (ratingValue <= 2.5)
            //reportButton.visibility = View.GONE
            ratingValue = RatingType.NONE
            return;
        }
        act.reportButton.visibility = View.VISIBLE;
        act.player_bottom_buttons_bar.visibility = View.VISIBLE
        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(act.player_bottom_rate_speaker)
        //player_bottom_rate_speaker.visibility = View.GONE
        act.videoPlayerView!!.controllerHideOnTouch = true
        act.videoPlayerView!!.controllerShowTimeoutMs = 5000;

        act.votePositiveButton.clearColorFilter()
        act.votePositiveButton.clearColorFilter()
    }


    private fun setFragmentArgument(frag: CommentReportBase) {
        val bundle = Bundle()
        bundle.putString("name", currentUser.name)
        bundle.putString("url", currentUser.url)
        bundle.putString("token", token)
        frag.arguments = bundle
    }

    public fun reportUser(v: View) {
        Companion.reportUser(currentUser.name, currentUser.url, token, act.fragmentManager, act.getString(R.string.report))
    }

    private fun commentUserRating(v: View) {
        val frag = UserCommentFragment()
        setFragmentArgument(frag)
        frag.show(act.fragmentManager, act.getString(R.string.send_comment))
    }

    companion object {
        public fun reportUser(name: String, url: String, token:String, fragmentManager: FragmentManager, message: String)
        {
            val frag = UserReportFragment()
            val bundle = Bundle()
            bundle.putString("name", name)
            bundle.putString("url", url)
            bundle.putString("token", token)
            frag.arguments = bundle
            frag.show(fragmentManager, message)
        }
    }


}