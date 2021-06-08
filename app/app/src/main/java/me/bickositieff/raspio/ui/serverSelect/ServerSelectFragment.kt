package me.bickositieff.raspio.ui.serverSelect

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import me.bickositieff.raspio.R
import me.bickositieff.raspio.databinding.FragmentServerControlsBinding
import me.bickositieff.raspio.databinding.FragmentServerSelectBinding

class ServerSelectFragment : Fragment() {

    private val viewModel: ServerSelectViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentServerSelectBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        return binding.root
    }
}