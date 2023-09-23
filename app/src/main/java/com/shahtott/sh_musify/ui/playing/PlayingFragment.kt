package com.shahtott.sh_musify.ui.playing

import android.content.ComponentName
import android.graphics.Bitmap
import android.media.session.MediaSession
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.MoreExecutors
import com.shahtott.sh_musify.R
import com.shahtott.sh_musify.common.core.BaseFragment
import com.shahtott.sh_musify.common.extentions.onBackPress
import com.shahtott.sh_musify.common.handler.MainAudioPlayer
import com.shahtott.sh_musify.common.handler.MusicHandler
import com.shahtott.sh_musify.common.handler.startAutoTextHorizontalScrolling
import com.shahtott.sh_musify.databinding.FragmentPlayingBinding
import com.shahtott.sh_musify.service.PlaybackService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayingFragment : BaseFragment<FragmentPlayingBinding>(
    FragmentPlayingBinding::inflate
) {
    private val viewModel: PlayingViewModel by viewModels()
    private lateinit var controller: MediaController

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

    override fun onPause() {
        super.onPause()
        val sessionToken = SessionToken(
            requireContext(),
            ComponentName(requireContext(), PlaybackService::class.java)
        )
        val controllerFuture = MediaController.Builder(requireContext(), sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                // init player
                controller = controllerFuture.get()
            },
            MoreExecutors.directExecutor()
        )
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