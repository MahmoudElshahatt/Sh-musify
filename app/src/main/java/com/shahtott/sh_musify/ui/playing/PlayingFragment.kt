package com.shahtott.sh_musify.ui.playing

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.shahtott.sh_musify.R
import com.shahtott.sh_musify.common.core.BaseFragment
import com.shahtott.sh_musify.common.extentions.showContentAboveStatusBar
import com.shahtott.sh_musify.common.extentions.showToast
import com.shahtott.sh_musify.common.handler.MainAudioPlayer
import com.shahtott.sh_musify.common.handler.MainAudioPlayer.seekToPlayBackPosition
import com.shahtott.sh_musify.common.handler.MainAudioPlayer.savePlayBackPosition
import com.shahtott.sh_musify.common.handler.MainAudioPlayer.togglePlayPauseBtn
import com.shahtott.sh_musify.common.handler.startAutoTextHorizontalScrolling
import com.shahtott.sh_musify.data.local.SharedPrefManager.Companion.PLAY_BACK_TIME
import com.shahtott.sh_musify.databinding.FragmentPlayingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayingFragment : BaseFragment<FragmentPlayingBinding>(
    FragmentPlayingBinding::inflate
) {
    private val viewModel: PlayingViewModel by viewModels()
    private val playingSoundUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().showContentAboveStatusBar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickListeners()
        observations()

    }

    private fun observations() {
        binding.apply {
            viewModel.musicEntity.observe(viewLifecycleOwner) { song ->
                scrollNameOfSongHorizontally(song.title)
                MainAudioPlayer.playAudio(
                    context = requireContext(),
                    audioPath = song.data,
                    seekBar = seekBar,
                    playPauseBtn = playPauseBtn,
                    R.drawable.ic_pause,
                    R.drawable.ic_play,
                    txtTimeToUpdate,
                    txtTotalTime,
                    onComplete = {
                        viewModel.onNextSongClick()
                    },
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sharedPref.savePlayBackPosition()
    }



    private fun onClickListeners() {
        binding.apply {
            playPauseBtn.setOnClickListener {
                togglePlayPauseBtn(
                    playPauseBtn = playPauseBtn,
                    R.drawable.ic_pause,
                    R.drawable.ic_play,
                )
            }
            ivNextSong.setOnClickListener {
                viewModel.onNextSongClick()
            }
            ivPrevSong.setOnClickListener {
                viewModel.onPrevSongClick()
            }
        }
    }

    private fun scrollNameOfSongHorizontally(text: String) {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val scrollertextview: TextView =
            requireActivity().findViewById(R.id.txt__details_song_name)
        scrollertextview.startAutoTextHorizontalScrolling(
            screenWidth,
            15000,
            text
        )

    }

}