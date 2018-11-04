package com.tsystems.r2b.dflow.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tsystems.r2b.dflow.data.repository.UserRepository

/**
 * Factory that creates LoginViewModel
 */
class LoginViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = LoginViewModel(userRepository) as T

}