package com.tlabscloud.r2b.dflow.screens.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.tlabscloud.r2b.dflow.MainActivity
import com.tlabscloud.r2b.dflow.util.Injector
import com.tlabscloud.r2b.dflow.databinding.LoginFragmentBinding


class LoginFragment : Fragment() {

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
            NavHostFragment.findNavController(this).popBackStack()
        }
        rootView = binding.root
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
