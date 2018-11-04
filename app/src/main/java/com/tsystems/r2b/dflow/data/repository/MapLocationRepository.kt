package com.tsystems.r2b.dflow.data.repository

import com.tsystems.r2b.dflow.data.local.dao.MapLocationDao
import com.tsystems.r2b.dflow.model.MapLocation
import org.jetbrains.anko.doAsync

class MapLocationRepository private constructor(private val mapLocationDao: MapLocationDao) {

    fun getAll() = mapLocationDao.load()

    fun create(mapLocation: MapLocation) =
        doAsync {
            mapLocationDao.create(mapLocation)
        }

    fun deleteAll() =
        doAsync {
            mapLocationDao.deleteAll()
        }

    companion object {
        @Volatile
        private var instance: MapLocationRepository? = null

        fun getInstance(mapLocationDao: MapLocationDao) =
            instance ?: synchronized(this) {
                instance ?: MapLocationRepository(mapLocationDao).also { instance = it }
            }
    }
}