package me.bickositieff.raspio.ui.serverControls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import me.bickositieff.raspio.databinding.FragmentServerControlsBinding

class ServerControlsFragment : Fragment() {

    private val serverControlsViewModel: ServerControlsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentServerControlsBinding.inflate(inflater, container, false)

        binding.transmissionState.setOnCheckedChangeListener { _, transmissionActive ->
            serverControlsViewModel.changeTransmissionState(
                transmissionActive
            )
        }

        return binding.root
    }
}