package com.example.dmd_project.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.dmd_project.R
import com.example.dmd_project.databinding.AddGdgFragmentBinding
import com.google.android.material.snackbar.Snackbar

// Fragment class for adding a GDG (Google Developer Group) entry
class AddGdgFragment : Fragment() {

    // Using activityViewModels to get a shared ViewModel instance scoped to the activity lifecycle
    private val viewModel: AddGdgViewModel by activityViewModels()

    // Called to create and return the fragment's UI view hierarchy
    override fun onCreateView(
        inflater: LayoutInflater,               // Inflater to inflate the fragment's view
        container: ViewGroup?,                  // Parent view to attach the fragment's UI to
        savedInstanceState: Bundle?             // Used to restore the fragment state if applicable
    ): View {
        // Inflate the layout using View Binding and set lifecycleOwner for LiveData observation
        val binding = AddGdgFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner  // Ensures LiveData updates respect the fragment's lifecycle
            this.viewModel = this@AddGdgFragment.viewModel  // Binds the ViewModel to the binding for data binding
        }

        // Handle the form submission
        binding.buttonSubmit.setOnClickListener {
            val name = binding.EditTextName.text.toString()
            val email = binding.EditTextEmail.text.toString()
            val city = binding.EditTextCity.text.toString()
            val country = binding.EditTextCountry.text.toString()
            val region = binding.EditTextRegion.text.toString()
            val why = binding.EditTextWhy.text.toString()

            if (name.isBlank() || city.isBlank() || country.isBlank() || region.isBlank() || why.isBlank()) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.error_empty_fields),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                viewModel.onSubmitApplication(name, email, city, country, region, why)
            }
        }

        // Observe the showSnackBarEvent LiveData to display a Snackbar when needed
        viewModel.showSnackBarEvent.observe(viewLifecycleOwner) { showSnackbar ->
            if (showSnackbar) {
                // Display a Snackbar with a success message
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.application_submitted),
                    Snackbar.LENGTH_SHORT
                ).show()

                // Notify the ViewModel that the Snackbar has been shown
                viewModel.doneShowingSnackbar()

                // Update the button's text and content description to reflect completion
                binding.buttonSubmit.apply {
                    contentDescription = getString(R.string.submitted)
                    text = getString(R.string.done)
                }
            }
        }

        // Indicate that this fragment will contribute to the options menu
        setHasOptionsMenu(true)

        // Return the root view of the binding object as the fragment's UI
        return binding.root
    }
}
