package me.bickositieff.raspio.ui.songSelect

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import me.bickositieff.raspio.R
import me.bickositieff.raspio.databinding.SongItemBinding
import me.bickositieff.raspio.ui.models.Song

class SongSelectAdapter(playlist: List<Song>, val viewModel: SongSelectViewModel) :
    RecyclerView.Adapter<SongSelectAdapter.ViewHolder>() {
    var playlist: List<Song> = playlist
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return playlist[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        SongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SongSelectAdapter.ViewHolder, position: Int) {
        holder.binding.song = playlist[position]
    }

    override fun getItemCount() = playlist.size

    inner class ViewHolder(
        val binding: SongItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { v ->
                val menu = PopupMenu(binding.root.context, v)
                menu.inflate(R.menu.popup_select_song)
                menu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popupNextSong -> {
                            viewModel.addAsNext(binding.song!!)
                        }
                        R.id.popupQueueSong -> {
                            viewModel.addToQueue(binding.song!!)
                        }
                        else -> {
                        }
                    }
                    true
                }
                menu.show()
            }

        }
    }
}