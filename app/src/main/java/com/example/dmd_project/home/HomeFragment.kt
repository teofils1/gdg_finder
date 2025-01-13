package com.example.dmd_project.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dmd_project.R
import com.example.dmd_project.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = HomeFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            this.viewModel = this@HomeFragment.viewModel
        }

        viewModel.navigateToSearch.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                findNavController().navigate(R.id.action_homeFragment_to_gdgListFragment)
                viewModel.onNavigatedToSearch()
            }
        }

        return binding.root
    }
}
