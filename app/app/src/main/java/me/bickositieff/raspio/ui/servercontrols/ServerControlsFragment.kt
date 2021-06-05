package me.bickositieff.raspio.ui.servercontrols

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import me.bickositieff.raspio.R

class ServerControlsFragment : Fragment() {

    private lateinit var serverControlsViewModel: ServerControlsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        serverControlsViewModel =
                ViewModelProvider(this).get(ServerControlsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_server_controls, container, false)

        serverControlsViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }
        return root
    }
}