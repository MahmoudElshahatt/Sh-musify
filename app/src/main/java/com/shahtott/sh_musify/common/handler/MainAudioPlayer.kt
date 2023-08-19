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


/** ///////// UNDER TESTING ///////// **/

/**
 * This object used to:
 * 00- play audio
 * 01- play a playlist
 * 02- repeat a playing sound
 */
object MainAudioPlayer {

    private const val tag = "MAudioPlayer"
    private var exoPlayer: ExoPlayer? = null
    private var nowPlayingAudio: String? = ""
    private var mSeekBarUpdateHandler: Handler? = null
    private var mUpdateSeekBar: Runnable? = null
    private var isPlayingPlaylist = false

    fun SharedPrefManager.savePlayBackPosition() {
        if (!nowPlayingAudio.isNullOrEmpty()) {
            write(
                SharedPrefManager.PLAY_BACK_TIME,
                exoPlayer?.currentPosition ?: 0L
            )
        }
    }


    /**
     * this method used to repeat the playing audio
     *
     * @param activity under testing , you can remove it and use Context
     * @param countOfRepeat the count of times you want to repeat the audio
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
     * @param onAudioChanged if the playlist moves to the next audio
     * @param onClickedAgain if user is playing a playlist and click on the button again
     * @param onNothingIsPlaying if noting is playing and user call repeat sound function
     *
     * Note:if noting is playing or [nowPlayingAudio] isEmpty or null then it will not work
     */
    fun repeatSound(
        activity: Activity,
        countOfRepeat: Int = 2,
        seekBar: SeekBar? = null,
        playPauseBtn: ImageView? = null,
        @DrawableRes playingIconRes: Int? = null,
        @DrawableRes pauseIconRes: Int? = null,
        textTvToUpdate: TextView? = null,
        totalTimeTv: TextView? = null,
        progressBar: ProgressBar? = null,
        onLoading: (() -> Unit)? = null,
        onError: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onAudioChanged: (() -> Unit)? = null,
        onClickedAgain: (() -> Unit)? = null,
        onNothingIsPlaying: (() -> Unit)? = null,
    ) {

        //check if noting is playing then return
        if (nowPlayingAudio == "") {
            onNothingIsPlaying?.invoke()
            return
        }

        //loop on count of repeat time and add it to list
        val listOfAudio = mutableListOf<String>()
        for (i in 0 until countOfRepeat) {
            listOfAudio.add(nowPlayingAudio ?: "")
        }

        playPlaylist(
            activity,
            listOfAudio,
            seekBar,
            playPauseBtn,
            playingIconRes,
            pauseIconRes,
            textTvToUpdate,
            totalTimeTv,
            progressBar,
            onLoading,
            onError,
            onComplete,
            onAudioChanged,
            onClickedAgain,
        )
    }

    /**
     * This method used to play a list of audios.
     *
     * @param activity under testing , you can remove it and use Context
     * @param listOfAudio the list of audio you want to play , it will be converted to a list of [MediaItem]
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
     * @param onAudioChanged if the playlist moves to the next audio
     * @param onClickedAgain if user is playing a playlist and click on the button again
     *
     * Note: some time duration of player returns with unknown value so i add check if it return with
     * more than 5 letters it returns with blank text you can change it.
     *
     * TODO: search for each place you add progressBar.visibility = View.Visible
     *       and add onLoading under it
     */
    fun playPlaylist(
        activity: Activity,
        listOfAudio: List<String>,
        seekBar: SeekBar? = null,
        playPauseBtn: ImageView? = null,
        @DrawableRes playingIconRes: Int? = null,
        @DrawableRes pauseIconRes: Int? = null,
        textTvToUpdate: TextView? = null,
        totalTimeTv: TextView? = null,
        progressBar: ProgressBar? = null,
        onLoading: (() -> Unit)? = null,
        onError: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onAudioChanged: (() -> Unit)? = null,
        onClickedAgain: (() -> Unit)? = null,
    ) {
        tryNow(tag) {

            if (isPlayingPlaylist) {
                exoPlayer?.stop()
                pauseIconRes?.let { playPauseBtn?.setImageResource(it) }
                isPlayingPlaylist = false
                onClickedAgain?.invoke()
                return@tryNow
            } else {
                exoPlayer?.play()
                playingIconRes?.let { playPauseBtn?.setImageResource(it) }
            }

            playPauseBtn?.visibility = View.INVISIBLE
            progressBar?.visibility = View.VISIBLE

            seekBar?.progress = 0

            //convert list of urls to list of media item
            val listOfMediaItem = mutableListOf<MediaItem>()
            listOfAudio.forEach {
                listOfMediaItem.add(MediaItem.fromUri(it))
            }


            //to pause any audio
            if (exoPlayer?.isPlaying == true) exoPlayer?.pause()

            exoPlayer = ExoPlayer.Builder(activity).build()
            exoPlayer?.playWhenReady = true

            listOfMediaItem.forEach { mediaItem ->
                exoPlayer?.addMediaItem(mediaItem)
            }

            exoPlayer?.prepare()
            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    onError?.invoke()
                    activity.showToast(error.message.toString())
                    Log.e("MainAudio", error.message.toString())
                    progressBar?.visibility = View.INVISIBLE
                    playPauseBtn?.visibility = View.VISIBLE
                    pauseIconRes?.let { playPauseBtn?.setImageResource(it) }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    prepareAndListenToPositionSeekBarChanges(
                        seekBar, (exoPlayer?.duration ?: 0).toInt(), textTvToUpdate
                    )
                    updateSeekBar(seekBar, textTvToUpdate)
                    totalTimeTv?.text = createTimeLabel(exoPlayer?.duration ?: 0)
                    onAudioChanged?.invoke()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            activity.showToast("Idle")
                        }

                        Player.STATE_BUFFERING -> {
                            activity.showToast("Buffering")
                            playPauseBtn?.visibility = View.INVISIBLE
                            progressBar?.visibility = View.VISIBLE
                        }
                        //onPrepared
                        Player.STATE_READY -> {
                            playPauseBtn?.visibility = View.VISIBLE
                            progressBar?.visibility = View.INVISIBLE
                            playingIconRes?.let { playPauseBtn?.setImageResource(it) }

                            prepareAndListenToPositionSeekBarChanges(
                                seekBar, (exoPlayer?.duration ?: 0).toInt(), textTvToUpdate
                            )
                            updateSeekBar(seekBar, textTvToUpdate)
                            totalTimeTv?.text = createTimeLabel(exoPlayer?.duration ?: 0)
                        }
                        //onComplete
                        Player.STATE_ENDED -> {
                            //reset all
                            onComplete?.invoke()
                            pauseIconRes?.let { playPauseBtn?.setImageResource(it) }
                            nowPlayingAudio = ""
                            mUpdateSeekBar?.let { mSeekBarUpdateHandler?.removeCallbacks(it) }
                        }

                        else -> {}
                    }
                }
            })
            exoPlayer?.play()
            isPlayingPlaylist = true
        }
    }


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
        return if (value.length == 5) value else ""
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
