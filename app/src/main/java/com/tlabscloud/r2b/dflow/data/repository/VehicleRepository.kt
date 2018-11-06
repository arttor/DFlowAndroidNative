package com.tlabscloud.r2b.dflow.data.repository

import com.tlabscloud.r2b.dflow.data.local.dao.VehicleDao
import com.tlabscloud.r2b.dflow.model.Vehicle
import org.jetbrains.anko.doAsync

class VehicleRepository private constructor(private val vehicleDao: VehicleDao) {

    fun getAll() = vehicleDao.load()

    fun create(vehicle: Vehicle) =
        doAsync {
            vehicleDao.create(vehicle)
        }

    fun deleteAll() =
        doAsync {
            vehicleDao.deleteAll()
        }

    companion object {
        @Volatile
        private var instance: VehicleRepository? = null

        fun getInstance(vehicleDao: VehicleDao) =
            instance ?: synchronized(this) {
                instance
                    ?: VehicleRepository(vehicleDao).also { instance = it }
            }
    }
}