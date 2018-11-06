package com.tlabscloud.r2b.dflow.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.tlabscloud.r2b.dflow.model.User


@Dao
interface UserDao {
    @Insert(onConflict = REPLACE)
    fun create(user: User)

    @Update
    fun update(user: User)


    @Query("DELETE FROM User")
    fun deleteAll()

    @Query("SELECT * FROM User")
    fun load(): LiveData<User>
}
