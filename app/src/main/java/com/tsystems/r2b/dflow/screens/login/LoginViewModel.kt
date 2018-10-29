package com.tsystems.r2b.dflow.screens.login

import androidx.lifecycle.ViewModel
import com.tsystems.r2b.dflow.data.repository.UserRepository
import com.tsystems.r2b.dflow.model.User
import java.util.*


class LoginViewModel : ViewModel() {
    var username: String?="asd"
    var password: String?=null

    fun login (){
        username?.let{
            UserRepository.create(User(it, Date()))
        }
    }
}
