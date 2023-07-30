package com.shahtott.sh_musify.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.shahtott.sh_musify.R
import com.shahtott.sh_musify.common.core.BaseFragment
import com.shahtott.sh_musify.common.handler.checkMusicPermissions
import com.shahtott.sh_musify.common.handler.onPermissionResult
import com.shahtott.sh_musify.databinding.FragmentMainBinding
import com.shahtott.sh_musify.ui.main.adapter.SongsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()
    private val songsAdapter: SongsAdapter = SongsAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        checkMusicPermissions(onPermissionGranted = {
            viewModel.fetchMusic()
        })
        observations()
    }

    private fun setUpAdapter() {
        binding.rvMain.apply {
            adapter = songsAdapter
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onPermissionResult(
            requestCode, permissions, grantResults,
            onPermissionGranted = { viewModel.fetchMusic() },
            onPermissionNotGranted = {

            }
        )

    }


    private fun observations() {
        viewModel.audioList.observe(viewLifecycleOwner) {
            //  Log.e("AudioList", it.toString())
            songsAdapter.submitList(it)
        }
    }


}