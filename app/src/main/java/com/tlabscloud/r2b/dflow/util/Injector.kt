package com.tlabscloud.r2b.dflow.util

import android.content.Context
import com.tlabscloud.r2b.dflow.MainViewModelFactory
import com.tlabscloud.r2b.dflow.data.local.DFlowDb
import com.tlabscloud.r2b.dflow.data.repository.UserRepository
import com.tlabscloud.r2b.dflow.data.repository.VehicleRepository
import com.tlabscloud.r2b.dflow.screens.login.LoginViewModelFactory
import com.tlabscloud.r2b.dflow.screens.searchVehicle.SearchVehicleViewModelFactory

/**
 * Helper for injection of datasources
 */
object Injector {

    // DAO

    fun getMapLocationDao(context: Context) = DFlowDb.getInstance(context).mapLocationDao()

    fun getUserDao(context: Context) = DFlowDb.getInstance(context).userDao()

    // Repositories

    fun getMapLocationRepository(context: Context) = VehicleRepository.getInstance(
        getMapLocationDao(context)
    )

    fun getUserRepository(context: Context) = UserRepository.getInstance(
        getUserDao(
            context
        )
    )

    // ViewModelFactories

    fun getMapViewModelFactory(context: Context) =
        SearchVehicleViewModelFactory(
            getMapLocationRepository(context)
        )

    fun getLoginViewModelFactory(context: Context) = LoginViewModelFactory(
        getUserRepository(context)
    )

    fun getMainViewModelFactory(context: Context) =
        MainViewModelFactory(getUserRepository(context))
}