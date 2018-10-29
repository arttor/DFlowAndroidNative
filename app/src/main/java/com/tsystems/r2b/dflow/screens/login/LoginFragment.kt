package com.tsystems.r2b.dflow.screens.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.tsystems.r2b.dflow.MainActivity
import com.tsystems.r2b.dflow.databinding.LoginFragmentBinding


class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.model = loginViewModel
        rootView = binding.root
        return rootView
    }

    override fun onDetach() {
        super.onDetach()
        showSoftwareKeyboard(false)
    }

    override fun onStart() {
        super.onStart()
        val act =requireActivity()as MainActivity
        act.lockDrawer(true)
    }
    override fun onStop() {
        super.onStop()
        val act =requireActivity()as MainActivity
        act.lockDrawer(false)
    }

    private fun showSoftwareKeyboard(showKeyboard: Boolean) {
        val activity = activity
        val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken,
            if (showKeyboard) InputMethodManager.SHOW_FORCED else InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

}
