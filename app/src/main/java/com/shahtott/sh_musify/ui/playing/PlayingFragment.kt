package com.shahtott.sh_musify.ui.playing

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shahtott.sh_musify.R
import com.shahtott.sh_musify.common.core.BaseFragment
import com.shahtott.sh_musify.common.extentions.showContentAboveStatusBar
import com.shahtott.sh_musify.databinding.FragmentPlayingBinding


class PlayingFragment : BaseFragment<FragmentPlayingBinding>(
    FragmentPlayingBinding::inflate
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().showContentAboveStatusBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_playing, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollNameOfSongHorizontally()

    }

    private fun scrollNameOfSongHorizontally() {
        val displayMetrics = DisplayMetrics()
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        val scrollertextview: TextView = requireActivity().findViewById(R.id.txt__details_song_name)
        scrollertextview.startAutoTextHorizentalScrolling(screenWidth, 10000, "Your scrolling text here")

    }

}