package com.tlabscloud.r2b.dflow.screens.bookVehicle

import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.tlabscloud.r2b.dflow.MainActivity
import com.tlabscloud.r2b.dflow.MainViewModel
import com.tlabscloud.r2b.dflow.R
import com.tlabscloud.r2b.dflow.databinding.FragmentVehicleBookingBinding


class VehicleBookingFragment : Fragment() {

    private lateinit var binding: FragmentVehicleBookingBinding

    private val mainViewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVehicleBookingBinding.inflate(inflater, container, false)
        binding.vehicle = mainViewModel.vehicleToBook
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
        setUpFadeInContentAnimation(binding.bookVehicleContent)
        setUpFadeInContentAnimation(binding.toolbar)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.loadingFragment,
                R.id.loginFragment,
                R.id.searchVehicleFragment
            ), (requireActivity() as MainActivity).drawerLayout
        )
        // Set up ActionBar
        binding.collapsingToolbar.setupWithNavController(binding.toolbar, findNavController(), appBarConfiguration)

        return binding.root
    }

    private fun setUpFadeInContentAnimation(content: View) {
        content.alpha = 0f
        val animator = ObjectAnimator.ofFloat(content, "alpha", 0f, 1f)
        animator.startDelay = 250
        animator.duration = 300
        animator.start()
    }
}
