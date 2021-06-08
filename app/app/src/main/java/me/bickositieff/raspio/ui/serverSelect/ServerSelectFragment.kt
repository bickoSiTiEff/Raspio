package me.bickositieff.raspio.ui.serverSelect

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.bickositieff.raspio.R

class ServerSelectFragment : Fragment() {

    companion object {
        fun newInstance() = ServerSelectFragment()
    }

    private lateinit var viewModel: ServerSelectViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_server_select, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ServerSelectViewModel::class.java)
        // TODO: Use the ViewModel
    }

}