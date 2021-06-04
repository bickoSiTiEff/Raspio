package me.bickositieff.raspio.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import me.bickositieff.raspio.R

class PlaylistFragment : Fragment() {

    private lateinit var playlistViewModel: PlaylistViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        playlistViewModel =
                ViewModelProvider(this).get(PlaylistViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_playlist, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.playlist_recycler)

        val textView: TextView = root.findViewById(R.id.song_title)
        playlistViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }
}