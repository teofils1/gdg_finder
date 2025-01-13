package com.example.dmd_project.search

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dmd_project.databinding.GdgListFragmentBinding
import com.google.android.gms.location.*
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.example.dmd_project.R
import androidx.core.widget.doOnTextChanged

private const val LOCATION_PERMISSION_REQUEST = 1
private const val LOCATION_PERMISSION = "android.permission.ACCESS_FINE_LOCATION"

// Fragment class for displaying a list of GDG chapters
class GdgListFragment : Fragment() {

    // Lazily initialize the ViewModel using ViewModelProvider
    private val viewModel: GdgListViewModel by lazy {
        ViewModelProvider(this).get(GdgListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,               // Inflater to inflate the layout
        container: ViewGroup?,                  // Optional parent view
        savedInstanceState: Bundle?             // Saved state, if any
    ): View {
        // Inflate the layout using View Binding and set lifecycleOwner for LiveData observation
        val binding = GdgListFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner  // Ensure LiveData updates respect the fragment's lifecycle
            viewModel = this@GdgListFragment.viewModel  // Bind the ViewModel to the layout for data binding
        }

        // Create and set the adapter for the RecyclerView, passing a click listener
        val adapter = GdgListAdapter(GdgClickListener { chapter ->
            // Open the chapter's website in a browser when an item is clicked
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(chapter.website)))
        })
        binding.gdgChapterList.adapter = adapter

        // Observe changes in the filtered list of chapters and submit the updated list to the adapter
        viewModel.filteredGdgList.observe(viewLifecycleOwner) { chapters ->
            adapter.submitList(chapters)  // Submit the new list to the adapter
        }

        // Listen for changes in the search bar input and update the search query in the ViewModel
        binding.searchBar.doOnTextChanged { text, _, _, _ ->
            viewModel.searchQuery.value = text.toString()  // Update the search query
        }

        // Observe changes in the showNeedLocation LiveData and display a Snackbar if needed
        viewModel.showNeedLocation.observe(viewLifecycleOwner) { show ->
            if (show) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.location_permission_snackbar),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        // Observe changes in the region list and dynamically create chips for each region
        viewModel.regionList.observe(viewLifecycleOwner) { data ->
            data?.let {
                val chipGroup = binding.regionList
                val inflater = LayoutInflater.from(chipGroup.context)
                val chips = data.map { regionName ->
                    // Inflate each chip and set up its behavior
                    (inflater.inflate(R.layout.region, chipGroup, false) as Chip).apply {
                        text = regionName  // Set the chip text to the region name
                        tag = regionName   // Set the tag for identification
                        setOnCheckedChangeListener { _, isChecked ->
                            viewModel.onFilterChanged(regionName, isChecked)  // Notify ViewModel of filter changes
                        }
                    }
                }
                chipGroup.removeAllViews()  // Remove old chips
                chips.forEach(chipGroup::addView)  // Add new chips to the ChipGroup
            }
        }

        setHasOptionsMenu(true)  // Indicate that this fragment has an options menu
        return binding.root  // Return the root view of the binding object
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Request the last known location or start location updates if unavailable
        requestLastLocationOrStartLocationUpdates()
    }

    /**
     * Requests location permission from the user.
     */
    private fun requestLocationPermission() {
        requestPermissions(arrayOf(LOCATION_PERMISSION), LOCATION_PERMISSION_REQUEST)
    }

    /**
     * Requests the last known location or starts location updates if unavailable.
     */
    private fun requestLastLocationOrStartLocationUpdates() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission if not granted
            requestLocationPermission()
            return
        }

        // Get the last known location using the FusedLocationProviderClient
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                viewModel.onLocationUpdated(it)  // Update the ViewModel with the last known location
            } ?: startLocationUpdates(fusedLocationClient)  // If no location, start updates
        }
    }

    /**
     * Starts periodic location updates.
     */
    private fun startLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        // Check if location permission is granted before starting updates
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        // Create a location request with low power priority and a 10-second interval
        val request = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 10_000).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // Update the ViewModel with the latest location
                locationResult.lastLocation?.let { viewModel.onLocationUpdated(it) }
            }
        }
        // Request location updates using the FusedLocationProviderClient
        fusedLocationClient.requestLocationUpdates(request, callback, null)
    }

    /**
     * Handles the result of the location permission request.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is granted, request the last location or start updates
            requestLastLocationOrStartLocationUpdates()
        }
    }
}
