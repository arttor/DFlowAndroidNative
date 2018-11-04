package com.tsystems.r2b.dflow.data.repository

import com.tsystems.r2b.dflow.data.local.dao.UserDao
import com.tsystems.r2b.dflow.model.User
import org.jetbrains.anko.doAsync

class UserRepository private constructor(private val userDao: UserDao) {
    fun get() = userDao.load()

    fun create(user: User) =
        doAsync {
            userDao.create(user)
        }

    fun update(user: User) =
        doAsync {
            userDao.update(user)
        }

    fun deleteAll() =
        doAsync {
            userDao.deleteAll()
        }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userDao: UserDao) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userDao).also { instance = it }
            }
    }
}