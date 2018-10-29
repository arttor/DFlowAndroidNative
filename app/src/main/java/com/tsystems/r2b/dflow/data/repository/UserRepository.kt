package com.tsystems.r2b.dflow.data.repository

import androidx.lifecycle.LiveData
import com.tsystems.r2b.dflow.data.local.DFlowDb
import com.tsystems.r2b.dflow.model.User
import org.jetbrains.anko.doAsync


object UserRepository {
    fun get(): LiveData<User> {
        return DFlowDb.dbInstance!!.userDao().load()
    }

    fun create(user: User) {
        doAsync {
            DFlowDb.dbInstance!!.userDao().create(user)
        }
    }

    fun update(user: User) {
        doAsync {
            DFlowDb.dbInstance!!.userDao().update(user)
        }
    }

    fun deleteAll() {
        doAsync {
            DFlowDb.dbInstance!!.userDao().deleteAll()
        }
    }
}