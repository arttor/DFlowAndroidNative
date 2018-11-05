package com.tsystems.r2b.dflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tsystems.r2b.dflow.data.repository.UserRepository
import com.tsystems.r2b.dflow.model.MapLocation
import com.tsystems.r2b.dflow.model.User

class MainViewModel constructor(userRepository: UserRepository) : ViewModel() {

    val user: LiveData<User> = userRepository.get()
    var vehicleToBook: MapLocation? = null
}
