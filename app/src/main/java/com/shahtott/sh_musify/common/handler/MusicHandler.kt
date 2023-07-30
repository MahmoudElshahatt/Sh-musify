package com.shahtott.sh_musify.common.handler

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.shahtott.sh_musify.models.AudioModel
import java.io.File

private const val READ_STORAGE_PERMISSION_REQUEST_CODE = 123
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


fun loadAlbumArt(albumId: Long, view: ImageView) {
    val artworkUri: Uri = Uri.parse("content://media/external/audio/albumart")
    val path: Uri = ContentUris.withAppendedId(artworkUri, albumId)
    Glide.with(view.context).load(path).into(view)
}

fun Application.fetchMusicFromDevice(): ArrayList<AudioModel> {
    val musicList = ArrayList<AudioModel>()
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DATA
    )
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

    val cursor = applicationContext.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        null,
        sortOrder
    )

    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        //val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
//        val albumArtColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)


        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            val title = it.getString(titleColumn)
            val artist = it.getString(artistColumn)
            val data = it.getString(dataColumn)
         //   val duration =it.getString(durationColumn)
         //   val albumArt =it.getString(albumArtColumn)

            val mediaFile = File(data)
            if (!mediaFile.path.contains("WhatsApp/Media")) {
                Log.e("MusicFetch", "ID: $id, Title: $title, Artist: $artist, Data: $data")
                musicList.add(AudioModel(id, data, title, artist))
            }
        }
    }

    return musicList
}



@SuppressLint("Range")
fun getSongDuration(context: Context, audioUri: String): Long {
    val contentResolver: ContentResolver = context.contentResolver
    val projection = arrayOf(MediaStore.Audio.Media.DURATION)
    val selection = "${MediaStore.Audio.Media.DATA} = ?"
    val selectionArgs = arrayOf(audioUri)
    contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val duration =
                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            return duration
        }
    }
    return 0L
}

fun formatDurationToMinutesSeconds(durationMillis: Long): String {
    val minutes = (durationMillis / 1000) / 60
    val seconds = (durationMillis / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
