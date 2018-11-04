package com.tsystems.r2b.dflow.screens.login

import androidx.lifecycle.ViewModel
import com.tsystems.r2b.dflow.data.repository.UserRepository
import com.tsystems.r2b.dflow.model.User
import java.util.*


class LoginViewModel constructor(private val userRepository: UserRepository) : ViewModel() {
    var username: String? = null
    var password: String? = null

    fun login() {
        username?.let {
            userRepository.create(User(it, Date()))
        }
    }
}
