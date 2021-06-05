package me.bickositieff.raspio.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import me.bickositieff.raspio.R

class PlaylistFragment : Fragment() {

    private lateinit var playlistViewModel: PlaylistViewModel
    private lateinit var mAdapter: SongAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        playlistViewModel =
                ViewModelProvider(this).get(PlaylistViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_playlist, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.playlist_recycler)

        mAdapter = SongAdapter(playlistViewModel.playlist.value!!)
        recyclerView.adapter = mAdapter

        playlistViewModel.playlist.observe(viewLifecycleOwner){
            mAdapter.playlist = it
        }

        return root
    }
}