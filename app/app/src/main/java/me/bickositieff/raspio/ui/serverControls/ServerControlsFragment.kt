package me.bickositieff.raspio.ui.serverControls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import me.bickositieff.raspio.databinding.FragmentServerControlsBinding

class ServerControlsFragment : Fragment() {

    private val viewModel: ServerControlsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentServerControlsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        binding.transmissionState.setOnCheckedChangeListener { _, transmissionActive ->
            viewModel.changeTransmissionState(
                transmissionActive
            )
        }

        return binding.root
    }
}