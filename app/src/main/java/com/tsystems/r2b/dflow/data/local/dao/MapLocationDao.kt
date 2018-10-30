package com.tsystems.r2b.dflow.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.tsystems.r2b.dflow.model.MapLocation


@Dao
interface MapLocationDao {
    @Insert(onConflict = REPLACE)
    fun create(user: MapLocation)

    @Update
    fun update(user: MapLocation)


    @Query("DELETE FROM MapLocation")
    fun deleteAll()

    @Query("SELECT * FROM MapLocation")
    fun load(): LiveData<List<MapLocation>>
}
