package com.shahtott.sh_musify.ui.main.adapter

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shahtott.sh_musify.common.handler.formatDurationToMinutesSeconds
import com.shahtott.sh_musify.common.handler.getSongDuration
import com.shahtott.sh_musify.common.handler.loadAlbumArt
import com.shahtott.sh_musify.databinding.ItemSongBinding
import com.shahtott.sh_musify.models.AudioModel

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
            loadAlbumArt(audio.id, binding.imgSong)
            binding.apply {
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

