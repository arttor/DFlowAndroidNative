package com.tlabscloud.r2b.dflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.tlabscloud.r2b.dflow.databinding.ActivityMainBinding
import com.tlabscloud.r2b.dflow.util.Injector
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    public lateinit var drawerLayout: DrawerLayout
   // private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        val factory = Injector.getMainViewModelFactory(this)
        ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.nav_fragment)
        mainViewModel.targetFragmentId = navController.currentDestination?.id

        mainViewModel.authAction.observe(this, Observer {
            when (it) {
                AuthAction.REDIRECT_LOGIN -> navController.navigate(R.id.loginFragment)
                AuthAction.LET_IN -> mainViewModel.targetFragmentId?.let { it1 ->
                    navController.popBackStack(
                        it1,
                        false
                    )
                }
                AuthAction.CHECK_TOKEN -> {
                    mainViewModel.checkToken()
                    navController.navigate(R.id.loadingFragment)
                }
                else -> {
                }
            }
        })

       drawerLayout = binding.drawerLayout


        // Set up navigation menu
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.nav_fragment), drawerLayout
        )
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
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
        supportFragmentManager.findFragmentById(R.id.nav_fragment)?.childFragmentManager
            ?.fragments?.get(0)?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun lockDrawer(lock: Boolean) {
        drawer_layout.setDrawerLockMode(if (lock) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}
