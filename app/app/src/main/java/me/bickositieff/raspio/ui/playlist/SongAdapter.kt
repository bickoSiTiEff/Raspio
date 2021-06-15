package me.bickositieff.raspio.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.bickositieff.raspio.databinding.SongItemBinding
import me.bickositieff.raspio.ui.models.Song

open class SongAdapter(
    playlist: List<Song>
) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {
    var playlist: List<Song> = playlist
    set(value){
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        SongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.song = playlist[position]
    }

    override fun getItemCount() = playlist.size

    inner class ViewHolder(val binding: SongItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
    }
}