package com.shahtott.sh_musify.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.shahtott.sh_musify.R
import com.shahtott.sh_musify.common.core.BaseFragment
import com.shahtott.sh_musify.common.handler.checkMusicPermissions
import com.shahtott.sh_musify.common.handler.onPermissionResult
import com.shahtott.sh_musify.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkMusicPermissions(onPermissionGranted = {
            viewModel.fetchMusic()
        })
        observations()
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

        }
    }


}