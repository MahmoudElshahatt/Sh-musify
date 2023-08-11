package com.shahtott.sh_musify.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shahtott.sh_musify.R
import com.shahtott.sh_musify.common.handler.MusicHandler.decodeBase64AndReturnBitmap
import com.shahtott.sh_musify.common.handler.MusicHandler.formatDurationToMinutesSeconds
import com.shahtott.sh_musify.common.handler.MusicHandler.getSongDuration
import com.shahtott.sh_musify.data.local.room.MusicEntity
import com.shahtott.sh_musify.databinding.ItemSongBinding

class SongsAdapter(
    private var onSongClicked: ((MusicEntity) -> Unit)? = null
) : ListAdapter<MusicEntity, SongsAdapter.SongViewHolder>(ListAdapterDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SongViewHolder(binding)
    }

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(audio: MusicEntity) {
            binding.apply {
                if (audio.imageBytes.isNotEmpty()) {
                    val imageBytes = decodeBase64AndReturnBitmap(audio.imageBytes)
                    Glide.with(itemView.context).load(imageBytes)
                        .placeholder(R.drawable.ic_music).centerCrop()
                        .error(R.drawable.ic_music)
                        .into(binding.imgSong)
                } else {
                    Glide.with(itemView.context).load(R.drawable.ic_music)
                        .fitCenter()
                        .into(binding.imgSong)
                }

                txtSongName.text = audio.title
                val durationInMillis = getSongDuration(itemView.context, audio.data)
                txtSongDuration.text = formatDurationToMinutesSeconds(durationInMillis)
                txtSongArtist.text = audio.artist

                binding.cons.setOnClickListener {
                    onSongClicked?.invoke(audio)
                }
            }
        }


    }


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class ListAdapterDiffUtil : DiffUtil.ItemCallback<MusicEntity>() {

        override fun areItemsTheSame(oldItem: MusicEntity, newItem: MusicEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MusicEntity, newItem: MusicEntity): Boolean {
            return oldItem == newItem
        }
    }

}

