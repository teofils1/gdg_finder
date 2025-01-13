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
import com.example.dmd_project.LanguageUtils

// Fragment class representing the home screen of the application
class HomeFragment : Fragment() {

    // Creates a new instance of HomeViewModel scoped to this fragment's lifecycle
    private val viewModel: HomeViewModel by viewModels()

    // Called to create and return the fragment's UI view hierarchy
    override fun onCreateView(
        inflater: LayoutInflater,               // Inflater to inflate the fragment's layout
        container: ViewGroup?,                  // Optional parent view to attach the fragment's UI to
        savedInstanceState: Bundle?             // Used to restore the fragment's previous state
    ): View {
        // Inflate the layout using View Binding and set lifecycleOwner for LiveData observation
        val binding = HomeFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner  // Ensures LiveData updates respect the fragment's lifecycle
            this.viewModel = this@HomeFragment.viewModel  // Binds the ViewModel to the binding for data binding
        }

        // Handle the language change button click
        binding.buttonChangeLanguage.setOnClickListener {
            val newLanguage = if (LanguageUtils.getSavedLanguage(requireContext()) == "en") "es" else "en"
            LanguageUtils.setLocale(requireContext(), newLanguage)
            requireActivity().recreate() // Restart activity to apply changes
        }

        // Observes the navigateToSearch LiveData to handle navigation to another fragment
        viewModel.navigateToSearch.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {  // Check if navigation should occur
                // Use NavController to navigate to the GDG list fragment
                findNavController().navigate(R.id.action_homeFragment_to_gdgListFragment)

                // Notify the ViewModel that the navigation has been handled
                viewModel.onNavigatedToSearch()
            }
        }

        // Return the root view of the binding object as the fragment's UI
        return binding.root
    }
}

