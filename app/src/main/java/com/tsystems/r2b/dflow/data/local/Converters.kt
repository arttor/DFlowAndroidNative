package com.tsystems.r2b.dflow.data.local

import androidx.room.TypeConverter
import com.tsystems.r2b.dflow.model.LocationType
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromLocationType(value: Int?): LocationType? = if (value == null) null else LocationType.values()[value]

    @TypeConverter
    fun toLocationType(locationType: LocationType?): Int? {
        return locationType?.ordinal
    }
}
