package com.example.dmd_project.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dmd_project.databinding.FragmentRequestsBinding

class RequestsFragment : Fragment() {

    private lateinit var binding: FragmentRequestsBinding
    private val viewModel: RequestsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        // Set up RecyclerView
        val adapter = RequestsAdapter()
        binding.recyclerViewRequests.adapter = adapter
        binding.recyclerViewRequests.layoutManager = LinearLayoutManager(requireContext())

        // Observe the list of requests and submit to the adapter
        viewModel.requests.observe(viewLifecycleOwner) { requests ->
            adapter.submitList(requests)
        }

        return binding.root
    }
}
