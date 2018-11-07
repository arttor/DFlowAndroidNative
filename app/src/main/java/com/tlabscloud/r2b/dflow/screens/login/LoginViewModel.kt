package com.tlabscloud.r2b.dflow.screens.login

import androidx.lifecycle.ViewModel
import com.tlabscloud.r2b.dflow.data.repository.UserRepository


class LoginViewModel constructor(private val userRepository: UserRepository) : ViewModel() {
    var username: String? = null
    var password: String? = null
}
