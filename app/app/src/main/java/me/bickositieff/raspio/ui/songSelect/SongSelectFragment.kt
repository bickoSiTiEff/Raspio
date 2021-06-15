package me.bickositieff.raspio.ui.songSelect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import me.bickositieff.raspio.R

class SongSelectFragment : Fragment() {


    private val viewModel: SongSelectViewModel by viewModels()
    private lateinit var mAdapter: SongSelectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_song_select, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.song_select_recycler)


        mAdapter = SongSelectAdapter(viewModel.availableSongs.value ?: emptyList(), viewModel)
        recyclerView.adapter = mAdapter

        viewModel.availableSongs.observe(viewLifecycleOwner){
            mAdapter.playlist = it
        }

        return root
    }
}