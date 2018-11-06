package com.tlabscloud.r2b.dflow.screens.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.tlabscloud.r2b.dflow.MainActivity
import com.tlabscloud.r2b.dflow.MainViewModel
import com.tlabscloud.r2b.dflow.R
import com.tlabscloud.r2b.dflow.databinding.LoginFragmentBinding
import com.tlabscloud.r2b.dflow.util.Injector


class LoginFragment : Fragment() {
    private val mainViewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        val factory = Injector.getMainViewModelFactory(requireContext())
        ViewModelProviders.of(requireActivity(), factory).get(MainViewModel::class.java)
    }

    private lateinit var binding: LoginFragmentBinding
    private lateinit var currentContext: Context

    private val loginViewModel: LoginViewModel by lazy(LazyThreadSafetyMode.NONE) {
        val factory = Injector.getLoginViewModelFactory(currentContext)
        ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
    }

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)

        currentContext = context ?: return binding.root

        binding.model = loginViewModel
        binding.login = View.OnClickListener {
            loginViewModel.login()
        }
        rootView = binding.root

        //TODO: check token in mock and redirect to searchVehicleFragment if ok or hide progerssBar and make login visible
        mainViewModel.user.observe(this, Observer {
            val navController = Navigation.findNavController(requireActivity(), R.id.nav_fragment)
            if (it == null) {
                // hide progressbar and make login visible
                binding.progressBarCyclic.visibility = View.GONE
                binding.loginContent.visibility = View.VISIBLE
            } else {
                // redirect to searchVehicleFragment
                navController.navigate(R.id.searchVehicleFragment)
            }
        })
        return rootView
    }

    override fun onStart() {
        super.onStart()
        val act = requireActivity() as MainActivity
        act.lockDrawer(true)
        act.supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        val act = requireActivity() as MainActivity
        act.lockDrawer(false)
        showSoftwareKeyboard(false)
        act.supportActionBar?.show()
    }

    private fun showSoftwareKeyboard(showKeyboard: Boolean) {
        val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus!!.windowToken,
            if (showKeyboard) InputMethodManager.SHOW_FORCED else InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

}
