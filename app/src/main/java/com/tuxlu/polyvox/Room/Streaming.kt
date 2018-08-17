package com.tuxlu.polyvox.Room

import android.net.Uri
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_room.*

class Streaming {
    private var streamUrl: String = ""
    private var player: SimpleExoPlayer? = null
    private lateinit var dataSourceFactory: DefaultDataSourceFactory


    fun setVideoPlayer(token: String, nUrl: String, act: Room) {
        if (UtilsTemp.isStringEmpty(nUrl) || nUrl == streamUrl)
            return

        act.videoPlayerView.visibility = View.VISIBLE
        val context = act.baseContext
        //Uri videoUrl = Uri.parse(APIUrl.BASE_URL + APIUrl.VIDEO_STREAM + "/" + id);
        //Uri videoUrl = Uri.parse("http://www-itec.uni-klu.ac.at/ftp/datasets/DASHDataset2014/BigBuckBunny/2sec/BigBuckBunny_2s_simple_2014_05_09.mpd");
        //Uri videoUrl = Uri.parse("http://vm2.dashif.org/livesim-dev/periods_60/xlink_30/insertad_3/testpic_2s/Manifest.mpd");
        var videoUrl: Uri = if (token == "black")
            Uri.parse("http://yt-dash-mse-test.commondatastorage.googleapis.com/media/feelings_vp9-20130806-manifest.mpd")
        else
            Uri.parse(token)
        //Uri.parse(APIUrl.BASE_URL + APIUrl.ROOM + token + APIUrl.ROOM_STREAM_SUFFIX);
        streamUrl = token

        if (player == null) {
            val bandwidthMeter = DefaultBandwidthMeter()
            dataSourceFactory = DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, act.resources.getString(R.string.app_name)), bandwidthMeter)
            // This is the MediaSource representing the media to be played.
            //val videoSource =  HlsMediaSource(videoUrl, dataSourceFactory,
            //DefaultDashChunkSource.Factory(dataSourceFactory), null, null)
            val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
            act.videoPlayerView!!.player = player
        }
        val videoSource = HlsMediaSource(videoUrl, dataSourceFactory,
                null, null)
        player!!.prepare(videoSource)
        player!!.playWhenReady = true

        val listener = object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}
            override fun onPlayerError(error: ExoPlaybackException?) {}
            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPositionDiscontinuity() {}
            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}
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
        player!!.addListener(listener)
    }

    fun stopPlayer() {
        player?.stop()
    }

    fun setPlayOnReady(boolean: Boolean) {
        player?.playWhenReady = boolean
    }
}