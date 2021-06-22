package me.bickositieff.raspio.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import me.bickositieff.raspio.R
import me.bickositieff.raspio.databinding.SongItemBinding
import me.bickositieff.raspio.ui.models.Song

class SongAdapter(
    playlist: List<Song>,
    val viewModel: PlaylistViewModel
) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {
    var playlist: List<Song> = playlist
    set(value){
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.song = playlist[position]
    }

    override fun getItemCount() = playlist.size

    inner class ViewHolder(
        val binding: SongItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { v ->
                val menu = PopupMenu(binding.root.context, v)
                menu.inflate(R.menu.popup_playlist_song)
                menu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popupMoveUp -> {
                            viewModel.moveUp(playlist.indexOf(binding.song!!), binding.song!!)
                        }
                        R.id.popupMoveDown -> {
                            viewModel.moveDown(playlist.indexOf(binding.song!!), binding.song!!)
                        }
                        R.id.popupSetAsNext -> {
                            viewModel.setAsNext(playlist.indexOf(binding.song!!), binding.song!!)
                        }
                        R.id.popupRemove -> {
                            viewModel.remove(playlist.indexOf(binding.song!!))
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
