package com.tlabscloud.r2b.dflow.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mapbox.mapboxsdk.geometry.LatLng

@Entity
data class Vehicle(
    @PrimaryKey val
    id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val chargingLevel: Double,
    val electricRange: Int,
    val seats: Int,
    @Embedded
    val tariff: VehicleTariff,
    val plate: String? = null
) {
    val latLng: LatLng
        get() = LatLng(latitude, longitude)
    val chargeLevelFormatted: String
        get() = "${chargingLevel*100}%"
}

data class VehicleTariff(
    val currency: String,
    val pricePerMinute: Double,
    val freeWaitTime: Int = 0
)