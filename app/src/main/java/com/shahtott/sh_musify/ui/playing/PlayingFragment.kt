package com.shahtott.sh_musify.ui.playing

import android.graphics.Bitmap
import android.media.session.MediaSession
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.shahtott.sh_musify.R
import com.shahtott.sh_musify.common.core.BaseFragment
import com.shahtott.sh_musify.common.extentions.onBackPress
import com.shahtott.sh_musify.common.handler.MainAudioPlayer
import com.shahtott.sh_musify.common.handler.MusicHandler
import com.shahtott.sh_musify.common.handler.startAutoTextHorizontalScrolling
import com.shahtott.sh_musify.databinding.FragmentPlayingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayingFragment : BaseFragment<FragmentPlayingBinding>(
    FragmentPlayingBinding::inflate
) {
    private val viewModel: PlayingViewModel by viewModels()

    @Inject
    lateinit var myPlayer: MainAudioPlayer


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPress {
            myPlayer.resetPlayer()
            findNavController().navigateUp()
        }
        onClickListeners()
        observations()

    }

    private fun observations() {
        binding.apply {
            viewModel.musicEntity.observe(viewLifecycleOwner) { song ->

                setSongImage(
                    MusicHandler.decodeBase64AndReturnBitmap(song.imageBytes)
                )
                scrollNameOfSongHorizontally(song.title)
                myPlayer.playAudio(
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

    private fun setSongImage(url: Bitmap) {
        Glide.with(this).load(url).placeholder(R.drawable.ic_music)
            .centerCrop()
            .into(binding.musicAlbumArt)
    }


    private fun onClickListeners() {
        binding.apply {
            playPauseBtn.setOnClickListener {
                myPlayer.togglePlayPauseBtn(
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