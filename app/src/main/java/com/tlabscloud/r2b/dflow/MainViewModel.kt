package com.tlabscloud.r2b.dflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tlabscloud.r2b.dflow.data.repository.UserRepository
import com.tlabscloud.r2b.dflow.model.User
import com.tlabscloud.r2b.dflow.model.Vehicle

class MainViewModel constructor(userRepository: UserRepository) : ViewModel() {

    val user: LiveData<User> = userRepository.get()
    var vehicleToBook: Vehicle? = null
}
