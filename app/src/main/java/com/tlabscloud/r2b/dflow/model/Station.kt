package com.tlabscloud.r2b.dflow.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mapbox.mapboxsdk.geometry.LatLng

@Entity
data class Station(
    @PrimaryKey val
    id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val workingHours: String,
    @Embedded
    val tariff: StationTariff
) {
    val latLng: LatLng
        get() = LatLng(latitude, longitude)
}

data class StationTariff(
    val currency: String,
    val pricePerMinute: Double,
    val pricePerKwH: Double
)