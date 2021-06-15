package me.bickositieff.raspio.ui.songSelect

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import me.bickositieff.raspio.R
import me.bickositieff.raspio.ui.playlist.PlaylistViewModel
import me.bickositieff.raspio.ui.playlist.SongAdapter

class SongSelectFragment : Fragment() {


    private val viewModel: SongSelectViewModel by viewModels()
    private lateinit var mAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_song_select, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.song_select_recycler)

        mAdapter = SongAdapter(viewModel.availableSongs.value ?: emptyList())
        recyclerView.adapter = mAdapter

        viewModel.availableSongs.observe(viewLifecycleOwner){
            mAdapter.playlist = it
        }

        return root
    }
}