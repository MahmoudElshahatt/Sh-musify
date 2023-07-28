package com.shahtott.sh_musify.common.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding


abstract class BaseFragment<VB : ViewBinding>(
    private val layoutInflater: (inflater: LayoutInflater) -> VB,
) : Fragment() {


    private var _binding: VB? = null
    val binding: VB
        get() = _binding as VB


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = layoutInflater(inflater)
        if (_binding == null) {
            throw java.lang.IllegalArgumentException("binding cannot be null")
        }
        return binding.root

    }


    fun <T> LiveData<T>.observeIfNotNull(observe: (T) -> Unit) {
        this.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            observe(it)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}