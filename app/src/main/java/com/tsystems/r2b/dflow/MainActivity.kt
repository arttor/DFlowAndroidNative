package com.tsystems.r2b.dflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.tsystems.r2b.dflow.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val mainViewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.user.observe(this, Observer {
            val navController = Navigation.findNavController(this, R.id.nav_fragment)
            if (it == null) {
                navController.navigate(R.id.loginFragment)
            }
        })

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        val navController = Navigation.findNavController(this, R.id.nav_fragment)
        //val appBarConfiguration = AppBarConfiguration(
        appBarConfiguration = AppBarConfiguration(setOf(R.id.loginFragment, R.id.mapFragment), drawerLayout)
        // Set up ActionBar

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Set up navigation menu
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.nav_fragment),appBarConfiguration
        )
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            val currentDestination = Navigation.findNavController(this, R.id.nav_fragment).currentDestination
            when (currentDestination?.id) {
                R.id.loginFragment -> {
                    finish()
                }
            }
            super.onBackPressed()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        supportFragmentManager.fragments.filter { it.isVisible }.forEach {
            it.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun lockDrawer(lock: Boolean) {
        drawer_layout.setDrawerLockMode(if (lock) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}
