package com.shahtott.sh_musify.ui.playing

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.shahtott.sh_musify.R
import com.shahtott.sh_musify.common.core.BaseFragment
import com.shahtott.sh_musify.common.extentions.showContentAboveStatusBar
import com.shahtott.sh_musify.common.handler.startAutoTextHorizontalScrolling
import com.shahtott.sh_musify.databinding.FragmentPlayingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayingFragment : BaseFragment<FragmentPlayingBinding>(
    FragmentPlayingBinding::inflate
) {
    private val viewModel: PlayingViewModel by viewModels()

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
        viewModel.musicEntity.observe(viewLifecycleOwner) { song ->
            scrollNameOfSongHorizontally(song.title)
        }
    }

    private fun onClickListeners() {
        binding.apply {

        }
    }

    private fun scrollNameOfSongHorizontally(text: String) {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        val scrollertextview: TextView = requireActivity().findViewById(R.id.txt__details_song_name)
        scrollertextview.startAutoTextHorizontalScrolling(
            screenWidth,
            13000,
            text
        )

    }

}