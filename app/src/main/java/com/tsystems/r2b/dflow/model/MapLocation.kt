package com.tsystems.r2b.dflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mapbox.mapboxsdk.geometry.LatLng

@Entity
data class MapLocation(
    @PrimaryKey val name: String,
    val type: LocationType,
    val latitude: Double,
    val longitude: Double,
    val charge: Double? = null,
    val tariff: String? = null,
    val imageUrl: String? = null
) {
    val latLng: LatLng
        get() = LatLng(latitude, longitude)
}

enum class LocationType { CAR, STATION }