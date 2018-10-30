package com.tsystems.r2b.dflow.data.repository

import androidx.lifecycle.LiveData
import com.tsystems.r2b.dflow.data.local.DFlowDb
import com.tsystems.r2b.dflow.model.MapLocation
import org.jetbrains.anko.doAsync


object MapLocationRepository {
    fun getAll(): LiveData<List<MapLocation>> {
        return DFlowDb.dbInstance!!.mapLocationDao().load()
    }

    fun create(mapLocation: MapLocation) {
        doAsync {
            DFlowDb.dbInstance!!.mapLocationDao().create(mapLocation)
        }
    }

    fun deleteAll() {
        doAsync {
            DFlowDb.dbInstance!!.mapLocationDao().deleteAll()
        }
    }
}