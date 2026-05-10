package com.example.fincurr

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fincurr.databinding.ActivityMainBinding
import com.example.fincurr.utils.PrefsManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsManager(this)

        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHost.navController
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        val startDestination = when {
            prefs.isLoggedIn && prefs.isPinVerified -> R.id.homeFragment
            prefs.isLoggedIn && !prefs.isPinVerified -> R.id.pinVerifyFragment
            else -> R.id.loginFragment
        }
        graph.setStartDestination(startDestination)
        navController.graph = graph

        binding.bottomNav.setupWithNavController(navController)

        val bottomDestinations = setOf(
            R.id.homeFragment,
            R.id.walletFragment,
            R.id.transactionsFragment,
            R.id.insightsFragment,
            R.id.profileFragment
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible = bottomDestinations.contains(destination.id)
        }
    }
}
