package me.bickositieff.raspio.ui.songSelect

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import me.bickositieff.raspio.R
import me.bickositieff.raspio.databinding.SongItemBinding
import me.bickositieff.raspio.ui.models.Song
import me.bickositieff.raspio.ui.playlist.SongAdapter

class SongSelectAdapter(playlist: List<Song>) : SongAdapter(playlist) {
    inner class ViewHolder(
        private val binding: SongItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnLongClickListener {v ->
                val menu = PopupMenu(binding.root.context, v)
                menu.inflate(R.menu.popup_select_song)
                menu.setOnMenuItemClickListener { item ->
                    when(item.itemId){
                        R.id.popupNextSong ->
                        {}
                        R.id.popupQueueSong ->
                        {}
                        else -> {}
                    }
                    true
                }

                true
            }
            
        }
    }
}