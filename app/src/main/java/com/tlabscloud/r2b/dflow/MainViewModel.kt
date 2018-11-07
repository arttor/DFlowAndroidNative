package com.tlabscloud.r2b.dflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tlabscloud.r2b.dflow.data.repository.UserRepository
import com.tlabscloud.r2b.dflow.model.User
import com.tlabscloud.r2b.dflow.model.Vehicle
import org.jetbrains.anko.doAsync
import java.util.*

class MainViewModel constructor(private val userRepository: UserRepository) : ViewModel() {

    val user: LiveData<User> = userRepository.get()
    var targetFragmentId: Int? = null
    val isTokenValid: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        this.postValue(false)
    }
    var vehicleToBook: Vehicle? = null
    val shouldRefresh: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        var tokenOk = false
        var userOk = false
        //this.postValue(false)
        this.addSource(user) {
            synchronized(tokenOk) {
                userOk = it != null
                if (!tokenOk && userOk) {
                    this.postValue(true)
                }
                if(!userOk){
                    this.postValue(false)
                }
            }
        }
        this.addSource(isTokenValid) {
            synchronized(tokenOk) {
                tokenOk = it
                if (!tokenOk && userOk) {
                    this.postValue(true)
                }
            }
        }
    }

    fun checkToken() {
        doAsync {
            // emulate check token

            Thread.sleep(2500)
            val tokenOk = false
            isTokenValid.postValue(tokenOk)
            if (!tokenOk) {
                userRepository.deleteAll()
            }

        }
    }

    fun login(login: String?, pwd: String?) {
        doAsync {
            login?.let {
                Thread.sleep(1500)
                userRepository.create(User(it, Date()))
                isTokenValid.postValue(true)
            }
        }
    }
}
