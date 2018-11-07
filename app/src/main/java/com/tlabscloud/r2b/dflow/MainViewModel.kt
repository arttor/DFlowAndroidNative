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
    val authAction: LiveData<AuthAction> = MediatorLiveData<AuthAction>().apply {
        var tokenValid = false
        var userPresented = false
        this.addSource(user) {
            synchronized(tokenValid) {
                userPresented = it != null
                when {
                    !userPresented -> this.postValue(AuthAction.REDIRECT_LOGIN)
                    userPresented && tokenValid -> this.postValue(AuthAction.LET_IN)
                    userPresented && !tokenValid -> this.postValue(AuthAction.CHECK_TOKEN)
                }
            }
        }
        this.addSource(isTokenValid) {
            synchronized(tokenValid) {
                tokenValid = it
                when {
                    tokenValid && userPresented -> this.postValue(AuthAction.LET_IN)
                    !tokenValid && userPresented -> this.postValue(AuthAction.CHECK_TOKEN)
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

enum class AuthAction { CHECK_TOKEN, REDIRECT_LOGIN, LET_IN }
