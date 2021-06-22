package me.bickositieff.raspio.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.bickositieff.raspio.R

class PlaylistFragment : Fragment() {

    private val playlistViewModel: PlaylistViewModel by viewModels()
    private lateinit var mAdapter: SongAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_playlist, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.playlist_recycler)

        val fab = root.findViewById<FloatingActionButton>(R.id.playlist_add_fab)
        fab.setOnClickListener { findNavController().navigate(R.id.navigation_song_select) }

        mAdapter = SongAdapter(playlistViewModel.playlist.value ?: emptyList(), playlistViewModel)
        recyclerView.adapter = mAdapter

        playlistViewModel.playlist.observe(viewLifecycleOwner){
            mAdapter.playlist = it
        }

        return root
    }
}
