package com.example.dmd_project

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dmd_project.databinding.ActivityMainBinding
import com.example.dmd_project.services.ChapterSyncService
import android.content.Intent

// MainActivity class that hosts the navigation and manages the UI for the app
class MainActivity : AppCompatActivity() {

    // Late-initialized binding object for ActivityMainBinding
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved language
        LanguageUtils.setLocale(this, LanguageUtils.getSavedLanguage(this))
        super.onCreate(savedInstanceState)
        // Set the content view using data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Setup navigation components
        setupNavigation()

        // Set default night mode to always use dark theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        startChapterSyncService()
    }

    private fun startChapterSyncService() {
        val intent = Intent(this, ChapterSyncService::class.java)
        startService(intent)
    }

    /**
     * Handles navigation when the hamburger menu or back button is pressed on the toolbar.
     * This delegates the action to the Navigation component.
     */
    override fun onSupportNavigateUp(): Boolean =
        navigateUp(findNavController(R.id.nav_host_fragment), binding.drawerLayout)

    /**
     * Configures navigation for the activity, including the toolbar, drawer layout, and navigation view.
     */
    private fun setupNavigation() {
        // Find the navigation controller associated with the NavHostFragment
        val navController = findNavController(R.id.nav_host_fragment)

        // Set up the toolbar as the action bar
        setSupportActionBar(binding.toolbar)

        // Set up the action bar with the navigation controller and link it to the DrawerLayout
        setupActionBarWithNavController(navController, binding.drawerLayout)

        // Link the navigation view (drawer) with the navigation controller
        binding.navigationView.setupWithNavController(navController)

        // Add a listener to handle changes in destination
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.gdg_search -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.gdg_search)
                    true
                }
                R.id.gdg_apply -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.gdg_apply)
                    true
                }
                R.id.menu_requests -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.requestsFragment)
                    true
                }
                R.id.menu_sync_now -> {
                    startChapterSyncService()
                    true
                }
                else -> false
            }
        }
    }
}