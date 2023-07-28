package com.shahtott.sh_musify.common.handler

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

private val READ_STORAGE_PERMISSION_REQUEST_CODE = 123
fun Fragment.checkMusicPermissions(
    onPermissionGranted: () -> Unit
) {
    if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Permission is not granted, request it
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_STORAGE_PERMISSION_REQUEST_CODE
        )
    } else {
        onPermissionGranted()

    }
}

fun onPermissionResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    onPermissionGranted: () -> Unit,
    onPermissionNotGranted: () -> Unit,
) {
    if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, fetch music
            onPermissionGranted()
        } else {
            // Permission denied,
            // handle accordingly (e.g., show a message or disable music-related functionality)
            onPermissionNotGranted()
        }
    }
}