package com.tsystems.r2b.dflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tsystems.r2b.dflow.data.repository.UserRepository
import com.tsystems.r2b.dflow.model.User


class MainViewModel : ViewModel() {

    val user: LiveData<User> = UserRepository.get()
}
