package com.shahtott.sh_musify.common.handler

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shahtott.sh_musify.R
import com.shahtott.sh_musify.common.handler.MusicHandler.decodeBase64
import com.shahtott.sh_musify.common.handler.MusicHandler.getAlbumArt
import com.shahtott.sh_musify.common.handler.MusicHandler.getSongUri
import com.shahtott.sh_musify.data.local.room.MusicEntity
import java.io.File

object MusicHandler {


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
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val data = it.getString(dataColumn)


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


    fun Context.getSongUri(songId: Long): Uri? {
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        val selection = MediaStore.Audio.Media._ID + " = ?"
        val selectionArgs = arrayOf(songId.toString())

        val cursor = contentResolver.query(contentUri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Audio.Media.DATA)
                val path = it.getString(columnIndex)
                return Uri.parse(path)
            }
        }
        return null
    }

    fun Context.getAlbumArt(uri: Uri): String? {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this, uri)
            val albumArt: ByteArray? = retriever.embeddedPicture
            retriever.release()

            if (albumArt != null) {
                return Base64.encodeToString(albumArt, Base64.DEFAULT)
            }
        } catch (e: Exception) {
            Log.e("AlbumArt", "Error getting album art: ${e.message}")
        }

        return null
    }

    fun ImageView.decodeBase64AndSetImage(base64: String) {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        Glide.with(context).load(decodedBitmap).placeholder(R.drawable.ic_music_white).centerCrop()
            .error(R.drawable.ic_music_white)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
    }

    fun decodeBase64AndReturnBitmap(imageBytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun decodeBase64(base64: String) = Base64.decode(base64, Base64.DEFAULT)


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
}

data class AudioModel(
    val id: Long,
    val data: String,
    val title: String,
    val artist: String,
    val duration: String = "00:00",
)

fun List<AudioModel>.toLocalMusicEntities(application: Application): List<MusicEntity> {
    val convertedData: ArrayList<MusicEntity> = ArrayList()
    map {
        val durationInMillis = MusicHandler.getSongDuration(application.applicationContext, it.data)
        val songUri = application.applicationContext.getSongUri(it.id)
        var finalAlbumArt: ByteArray = byteArrayOf()
        songUri?.let {
            val base64 = application.applicationContext.getAlbumArt(songUri) ?: ""
            finalAlbumArt = decodeBase64(base64)
        }
        convertedData.add(
            MusicEntity(
                data = it.data,
                title = it.title,
                artist = it.artist,
                duration = MusicHandler.formatDurationToMinutesSeconds(durationInMillis),
                imageBytes = finalAlbumArt
            )
        )
    }
    return convertedData
}