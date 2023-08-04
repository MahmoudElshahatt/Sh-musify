package com.shahtott.sh_musify.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shahtott.sh_musify.common.handler.AudioModel
import com.shahtott.sh_musify.common.handler.MusicHandler.decodeBase64AndSetImage
import com.shahtott.sh_musify.common.handler.MusicHandler.formatDurationToMinutesSeconds
import com.shahtott.sh_musify.common.handler.MusicHandler.getAlbumArt
import com.shahtott.sh_musify.common.handler.MusicHandler.getSongDuration
import com.shahtott.sh_musify.common.handler.MusicHandler.getSongUri
import com.shahtott.sh_musify.databinding.ItemSongBinding

class SongsAdapter(
    private var onSongClicked: ((AudioModel) -> Unit)? = null
) : ListAdapter<AudioModel, SongsAdapter.SongViewHolder>(ListAdapterDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SongViewHolder(binding)
    }

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(audio: AudioModel) {
            binding.apply {

                val songUri = itemView.context.getSongUri(audio.id)

                songUri?.let {
                    val albumArt = itemView.context.getAlbumArt(songUri)
                    if (albumArt != null) {
                        Log.d("AlbumArt", "Album Art: $albumArt")
                       binding.imgSong.decodeBase64AndSetImage(albumArt)
                    } else {
                        Log.d("AlbumArt", "Album Art not found")
                    }
                }

                txtSongName.text = audio.title
                val durationInMillis = getSongDuration(itemView.context, audio.data)
                txtSongDuration.text = formatDurationToMinutesSeconds(durationInMillis)
                txtSongArtist.text = audio.artist
            }
        }


    }


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class ListAdapterDiffUtil : DiffUtil.ItemCallback<AudioModel>() {

        override fun areItemsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
            return oldItem == newItem
        }
    }

}

