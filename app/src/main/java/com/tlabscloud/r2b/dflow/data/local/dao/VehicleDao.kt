package com.tlabscloud.r2b.dflow.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.tlabscloud.r2b.dflow.model.Vehicle


@Dao
interface VehicleDao {
    @Insert(onConflict = REPLACE)
    fun create(user: Vehicle)

    @Update
    fun update(user: Vehicle)


    @Query("DELETE FROM Vehicle")
    fun deleteAll()

    @Query("SELECT * FROM Vehicle")
    fun load(): LiveData<List<Vehicle>>
}
