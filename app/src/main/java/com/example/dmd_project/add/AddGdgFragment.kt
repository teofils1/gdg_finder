package com.example.dmd_project.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.dmd_project.R
import com.example.dmd_project.databinding.AddGdgFragmentBinding
import com.google.android.material.snackbar.Snackbar

class AddGdgFragment : Fragment() {

    private val viewModel: AddGdgViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AddGdgFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            this.viewModel = this@AddGdgFragment.viewModel
        }

        viewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer { showSnackbar ->
            if (showSnackbar) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.application_submitted),
                    Snackbar.LENGTH_SHORT
                ).show()
                viewModel.doneShowingSnackbar()
                binding.button.apply {
                    contentDescription = getString(R.string.submitted)
                    text = getString(R.string.done)
                }
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }
}

