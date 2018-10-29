package com.tsystems.r2b.dflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tsystems.r2b.dflow.data.local.dao.UserDao
import com.tsystems.r2b.dflow.model.User

@Database(entities = arrayOf(User::class), version = 1)
@TypeConverters(Converters::class)
abstract class DFlowDb : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private const val databaseName = "d-flow-db"

        var dbInstance: DFlowDb? = null
        fun init(context: Context) {
            if (dbInstance == null) {
                //TODO:uncomment to clean db on application start. add schema versioning later
                context.deleteDatabase(databaseName)
                dbInstance = Room.databaseBuilder(context, DFlowDb::class.java, databaseName).build()
            }
        }
    }
}