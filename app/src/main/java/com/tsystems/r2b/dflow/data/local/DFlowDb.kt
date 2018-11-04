package com.tsystems.r2b.dflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tsystems.r2b.dflow.data.local.dao.MapLocationDao
import com.tsystems.r2b.dflow.data.local.dao.UserDao
import com.tsystems.r2b.dflow.model.MapLocation
import com.tsystems.r2b.dflow.model.User

@Database(entities = [User::class, MapLocation::class], version = 1)
@TypeConverters(Converters::class)
abstract class DFlowDb : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mapLocationDao(): MapLocationDao

    companion object {
        private const val DATABASE_NAME = "d-flow-db"

        @Volatile
        private var dbInstance: DFlowDb? = null

        fun getInstance(context: Context): DFlowDb {
            return dbInstance ?: synchronized(this) {
                dbInstance ?: init(context).also { dbInstance = it }
            }
        }

        private fun init(context: Context): DFlowDb {
            //TODO:uncomment to clean db on application start. add schema versioning later
            context.deleteDatabase(DATABASE_NAME)
            return Room.databaseBuilder(context, DFlowDb::class.java, DATABASE_NAME).build()
        }
    }
}