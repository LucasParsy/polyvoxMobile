package com.tuxlu.polyvox.Room

import android.net.Uri
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.cast.framework.CastButtonFactory
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.exo_stream_playback_control.*
import com.google.android.gms.cast.MediaQueueItem
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.common.images.WebImage
import com.google.android.gms.cast.MediaMetadata.MEDIA_TYPE_MOVIE
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState


class Streaming// This is the MediaSource representing the media to be played.
//val videoSource =  HlsMediaSource(videoUrl, dataSourceFactory,
//DefaultDashChunkSource.Factory(dataSourceFactory), null, null)
(act: RoomBase, castContext: CastContext, title: String, imageUrl: String) {
    private var streamUrl: String = ""
    private var player: SimpleExoPlayer? = null
    private var dataSourceFactory: DefaultDataSourceFactory
    private lateinit var castPlayer: CastPlayer
    private lateinit var castMovieMetadata : MediaMetadata

    init {
        val context = act.baseContext
        CastButtonFactory.setUpMediaRouteButton(act, act.media_route_button)
        castSetup(title, imageUrl, castContext)
        if (castContext.castState != CastState.NO_DEVICES_AVAILABLE)
            act.media_route_button.visibility = View.VISIBLE
        castContext.addCastStateListener { state ->
            if (state == CastState.NO_DEVICES_AVAILABLE)
                act.media_route_button.visibility = View.GONE
            else {
                if (act.media_route_button.visibility == View.GONE)
                    act.media_route_button.visibility = View.VISIBLE
            }
        }
        dataSourceFactory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, act.resources.getString(R.string.app_name)))
        player = ExoPlayerFactory.newSimpleInstance(context)
        act.videoPlayerView!!.player = player
    }

    fun setVideoPlayer(token: String, nUrl: String, act: RoomBase) {
        if (UtilsTemp.isStringEmpty(nUrl) || nUrl == streamUrl)
            return

        act.videoPlayerView.visibility = View.VISIBLE
        //Uri videoUrl = Uri.parse(APIUrl.BASE_URL + APIUrl.VIDEO_STREAM + "/" + id);
        //Uri videoUrl = Uri.parse("http://www-itec.uni-klu.ac.at/ftp/datasets/DASHDataset2014/BigBuckBunny/2sec/BigBuckBunny_2s_simple_2014_05_09.mpd");
        //Uri videoUrl = Uri.parse("http://vm2.dashif.org/livesim-dev/periods_60/xlink_30/insertad_3/testpic_2s/Manifest.mpd");
        val videoUrl: Uri = if (token == "black")
            Uri.parse("http://yt-dash-mse-test.commondatastorage.googleapis.com/media/feelings_vp9-20130806-manifest.mpd")
        else
            Uri.parse(nUrl)
        //Uri.parse(APIUrl.BASE_URL + APIUrl.ROOM + token + APIUrl.ROOM_STREAM_SUFFIX);
        streamUrl = nUrl
        castChangeUrl(videoUrl.toString())
        val videoSource = HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(videoUrl)
        player!!.prepare(videoSource)
        player!!.playWhenReady = true

        val listener = object : Player.DefaultEventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == Player.STATE_READY) {//playing {
                    act.videoPlayerView.visibility = View.VISIBLE
                    act.bufferingProgress.visibility = View.INVISIBLE
                    act.roomWaitingLayout.visibility = View.INVISIBLE
                } else if (playWhenReady) //buffering
                {
                    act.bufferingProgress.visibility = View.VISIBLE
                } else {
                    act.videoPlayerView.visibility = View.INVISIBLE
                    act.roomWaitingLayout.visibility = View.VISIBLE
                }
            }
        }
        player!!.removeListener(null)
        player!!.addListener(listener)
    }

    fun addListener(listener: Player.EventListener) {
        player!!.addListener(listener)
    }

    private fun castSetup(title: String, imageUrl: String, castContext: CastContext) {
        //todo: verify that the url we set hasn't to be changed.
        //for now, this method is only called once.
        castMovieMetadata = MediaMetadata(MEDIA_TYPE_MOVIE)
        castMovieMetadata.putString(MediaMetadata.KEY_TITLE, title)
        castMovieMetadata.addImage(WebImage(Uri.parse(imageUrl)))

        castPlayer = CastPlayer(castContext)
        //todo: castPlayer.release() at end
    }

    fun castChangeUrl(videoUrl: String)
    {
        val mediaInfo = MediaInfo.Builder(videoUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_UNKNOWN)
                .setMetadata(castMovieMetadata).build()

        val mediaItems = arrayOf(MediaQueueItem.Builder(mediaInfo).build())
        castPlayer.setSessionAvailabilityListener(object : CastPlayer.SessionAvailabilityListener {
            override fun onCastSessionAvailable() {
                castPlayer.loadItems(mediaItems, 0, 0, Player.REPEAT_MODE_OFF)
            }

            override fun onCastSessionUnavailable() {}
        })
    }

    fun stopPlayer() {
        player?.stop()
    }

    fun setPlayOnReady(boolean: Boolean) {
        player?.playWhenReady = boolean
    }
}