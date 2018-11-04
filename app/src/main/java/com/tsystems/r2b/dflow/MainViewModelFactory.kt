package com.tsystems.r2b.dflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tsystems.r2b.dflow.data.repository.UserRepository

/**
 * Factory that creates MainViewModel
 */
class MainViewModelFactory constructor(private val userRepository: UserRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = MainViewModel(userRepository) as T

}