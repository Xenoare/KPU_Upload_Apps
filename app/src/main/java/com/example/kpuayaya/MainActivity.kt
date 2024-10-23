package com.example.kpuayaya

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.kpuayaya.databinding.ActivityMainBinding

// Implement Splashscreen
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var keepOnSplashScreen = true
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
            .setKeepOnScreenCondition { keepOnSplashScreen }
        Handler(Looper.getMainLooper()).postDelayed(({ keepOnSplashScreen = false }), 1500)


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.progressFragment)
        )

        setupBottomNavigationMenu()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideMenus =
                destination.id == R.id.loginFragment || destination.id == R.id.registerFragment || destination.id == R.id.uploadFragment || destination.id == R.id.updateFragment
            hideNavigation(hideMenus)
        }

    }

    private fun hideNavigation(hideMenus: Boolean) {
        if (hideMenus) binding.bottomNavView.visibility = View.GONE
        else binding.bottomNavView.visibility = View.VISIBLE
    }

    private fun setupBottomNavigationMenu() {
        val bottomNavView = binding.bottomNavView
        bottomNavView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

}