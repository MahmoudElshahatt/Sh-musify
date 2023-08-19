package com.shahtott.sh_musify.common.handler

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.shahtott.sh_musify.common.extentions.showToast
import com.shahtott.sh_musify.common.extentions.tryNow
import com.shahtott.sh_musify.data.local.SharedPrefManager



/**
 * This object used to:
 * 00- play audio
 */
object MainAudioPlayer {

    private const val tag = "MAudioPlayer"
    private var exoPlayer: ExoPlayer? = null
    private var nowPlayingAudio: String? = ""
    private var mSeekBarUpdateHandler: Handler? = null
    private var mUpdateSeekBar: Runnable? = null
    private var isPlayingPlaylist = false

    /**
     * This method used to play a list of audios.
     *
     * @param context context where you want to play audio
     * @param audioPath the audio you want to play
     * @param seekBar the seekBar you want to move it while playing and control the audio
     * @param playPauseBtn the icon used to play and pause audio that [playingIconRes] and [pauseIconRes] applied on it
     * @param playingIconRes the icon showed when player is playing
     * @param pauseIconRes the icon showed when player is stopped or pause
     * @param textTvToUpdate the textView that displays the current time of playing audio
     * @param totalTimeTv the textView that display all time of audio
     * @param progressBar the progressbar that used while loading
     * @param onLoading
     * @param onError when error happened while playing audio
     * @param onComplete when the playlist completed
     **
     * TODO: search for each place you add progressBar.visibility = View.Visible
     *       and add onLoading under it
     */
    fun playAudio(
        context: Context,
        audioPath: String,
        seekBar: SeekBar? = null,
        playPauseBtn: ImageView? = null,
        @DrawableRes playingIconRes: Int? = null,
        @DrawableRes pauseIconRes: Int? = null,
        textTvToUpdate: TextView? = null,
        totalTimeTv: TextView? = null,
        onLoading: (() -> Unit)? = null,
        onError: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
    ) {
        tryNow(tag) {

            isPlayingPlaylist = false

            //to pause any audio
            if (exoPlayer?.isPlaying == true) exoPlayer?.pause()

            seekBar?.progress = 0

            nowPlayingAudio = audioPath
            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer?.playWhenReady = true

            // Build the media item.
            val mediaItem = MediaItem.fromUri(nowPlayingAudio ?: "")
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    onError?.invoke()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    when (playbackState) {
                        //onPrepared
                        Player.STATE_READY -> {
                            playPauseBtn?.visibility = View.VISIBLE
                            playingIconRes?.let { playPauseBtn?.setImageResource(it) }

                            prepareAndListenToPositionSeekBarChanges(
                                seekBar, (exoPlayer?.duration ?: 0).toInt(), textTvToUpdate
                            )
                            updateSeekBar(seekBar, textTvToUpdate)
                            totalTimeTv?.text = createTimeLabel(exoPlayer?.duration ?: 0)
                        }
                        //onComplete
                        Player.STATE_ENDED -> {
                            onComplete?.invoke()
                            //reset all
                            reset(pauseIconRes, playPauseBtn)
                        }

                        else -> {}
                    }
                }
            })
            exoPlayer?.play()
        }
    }

    private fun reset(pauseIconRes: Int?, playPauseBtn: ImageView?) {
        pauseIconRes?.let { playPauseBtn?.setImageResource(it) }
        nowPlayingAudio = ""
        mUpdateSeekBar?.let { mSeekBarUpdateHandler?.removeCallbacks(it) }
        exoPlayer?.pause()
    }

    fun togglePlayPauseBtn(
        playPauseBtn: ImageView? = null,
        @DrawableRes playingIconRes: Int? = null,
        @DrawableRes pauseIconRes: Int? = null,
    ) {
        if (exoPlayer?.isPlaying == true) {
            exoPlayer?.pause()
            playPauseBtn?.animate()?.alpha(1f)?.duration = 1000
            pauseIconRes?.let { playPauseBtn?.setImageResource(it) }
        } else {
            exoPlayer?.play()
            playPauseBtn?.animate()?.alpha(1f)?.duration = 1000
            playingIconRes?.let { playPauseBtn?.setImageResource(it) }
        }
    }

    private fun prepareAndListenToPositionSeekBarChanges(
        seekBar: SeekBar?, totalTime: Int, textTvToUpdate: TextView?
    ) {
        seekBar?.apply {
            max = totalTime
        }
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                if (p2) {
                    seekBar.progress = progress
                    exoPlayer?.seekTo(progress.toLong())
                    exoPlayer?.play()
                    textTvToUpdate?.text = createTimeLabel(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    private fun updateSeekBar(
        seekBar: SeekBar?, textTvToUpdate: TextView?
    ) {
        tryNow(tag) {
            mSeekBarUpdateHandler = Handler(Looper.getMainLooper())
            mUpdateSeekBar = Runnable {
                val currentPosition = exoPlayer?.currentPosition
                textTvToUpdate?.text = createTimeLabel(currentPosition!!)
                seekBar?.progress = currentPosition.toInt()
                mSeekBarUpdateHandler?.postDelayed(mUpdateSeekBar!!, 50)
            }
            mSeekBarUpdateHandler?.postDelayed(mUpdateSeekBar!!, 0)
        }
    }

    fun createTimeLabel(time: Long): String {
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        val value = "%02d:%02d".format(min, sec)
        return if (value.length == 5) value else {
            val h = time / 1000 / 60 / 60
            val min = time / 1000 % 60
            "%02d:%02d:%02d".format(h, min, sec)
        }
    }

    fun updateSeekBarFromExoPlayerPosition(
        seekBar: SeekBar?,
        position: Long,
    ) {
        val duration = exoPlayer?.duration
        duration?.let { duration ->
            if (duration != C.TIME_UNSET && duration > 0) {
                val progress = (position * 100 / duration).toInt()
                seekBar?.progress = progress
            }
        }
    }

    fun resetPlayer() {
        nowPlayingAudio = ""
        mUpdateSeekBar?.let { mSeekBarUpdateHandler?.removeCallbacks(it) }
        exoPlayer?.stop()
        exoPlayer = null
    }

}
