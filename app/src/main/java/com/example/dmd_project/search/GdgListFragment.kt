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

class GdgListFragment : Fragment() {

    private val viewModel: GdgListViewModel by lazy {
        ViewModelProvider(this).get(GdgListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = GdgListFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@GdgListFragment.viewModel
        }

        val adapter = GdgListAdapter(GdgClickListener { chapter ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(chapter.website)))
        })
        binding.gdgChapterList.adapter = adapter

        // Observe filteredGdgList for changes
        viewModel.filteredGdgList.observe(viewLifecycleOwner) { chapters ->
            adapter.submitList(chapters)
        }

        // Listen to the search bar input
        binding.searchBar.doOnTextChanged { text, _, _, _ ->
            viewModel.searchQuery.value = text.toString()
        }

        viewModel.showNeedLocation.observe(viewLifecycleOwner) { show ->
            if (show) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.location_permission_snackbar),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        viewModel.regionList.observe(viewLifecycleOwner) { data ->
            data?.let {
                val chipGroup = binding.regionList
                val inflater = LayoutInflater.from(chipGroup.context)
                val chips = data.map { regionName ->
                    (inflater.inflate(R.layout.region, chipGroup, false) as Chip).apply {
                        text = regionName
                        tag = regionName
                        setOnCheckedChangeListener { _, isChecked ->
                            viewModel.onFilterChanged(regionName, isChecked)
                        }
                    }
                }
                chipGroup.removeAllViews()
                chips.forEach(chipGroup::addView)
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLastLocationOrStartLocationUpdates()
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(LOCATION_PERMISSION), LOCATION_PERMISSION_REQUEST)
    }

    private fun requestLastLocationOrStartLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                viewModel.onLocationUpdated(it)
            } ?: startLocationUpdates(fusedLocationClient)
        }
    }

    private fun startLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 10_000).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { viewModel.onLocationUpdated(it) }
            }
        }
        fusedLocationClient.requestLocationUpdates(request, callback, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            requestLastLocationOrStartLocationUpdates()
        }
    }
}
