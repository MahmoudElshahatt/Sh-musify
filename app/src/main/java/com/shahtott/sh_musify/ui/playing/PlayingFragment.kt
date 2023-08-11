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
import com.shahtott.sh_musify.common.handler.AnimationAutoTextScroller
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
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels

        val scrollertextview: TextView = requireActivity().findViewById(R.id.txt_song_name)
        val textscroller = AnimationAutoTextScroller(scrollertextview, width.toFloat())
        textscroller.setScrollingText(scrollertextview.text.toString())
        textscroller.start()
    }

}