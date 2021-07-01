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
    private lateinit var binding: FragmentServerControlsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentServerControlsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        binding.transmissionState.setOnCheckedChangeListener { _, transmissionActive ->
            if (!changeTransmissionState(transmissionActive, binding.frequencyEditText.text.toString())) {
                binding.transmissionState.isChecked = !transmissionActive
            }

        }

        return binding.root
    }

    private fun changeTransmissionState(on: Boolean, frequency: String): Boolean {
        if (viewModel.transmissionOn.value == on) return false
        val frequencyNum = try {
            frequency.toInt()
        } catch (e: NumberFormatException) {
            binding.frequency.error = "Must be a number!"
            return false
        }
        if (frequencyNum < 76 || frequencyNum > 108) {
            binding.frequency.error = "Must be between 76 and 108!"
            return false
        }
        binding.frequency.error = null
        viewModel.changeTransmissionState(on, frequencyNum)
        viewModel.transmissionOn.value = on
        return true
    }
}