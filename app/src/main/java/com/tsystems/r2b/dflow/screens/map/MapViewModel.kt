package com.tsystems.r2b.dflow.screens.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tsystems.r2b.dflow.data.repository.MapLocationRepository
import com.tsystems.r2b.dflow.model.LocationType
import com.tsystems.r2b.dflow.model.MapLocation


class MapViewModel : ViewModel() {
    val locations: LiveData<List<MapLocation>> = MapLocationRepository.getAll()



    fun getNearbyLocations(userLocation: LatLng){
        MapLocationRepository.create(MapLocation("Jaguar E Pace", LocationType.CAR,
            userLocation.latitude+0.02,
            userLocation.longitude+0.01,0.6,"5\$ per hour",
            "http://grantandgreen.de/wp-content/uploads/2018/03/Jag-E-Pace-BS-Blue.jpg"))
        MapLocationRepository.create(MapLocation("Tesla", LocationType.CAR,
            userLocation.latitude-0.05,
            userLocation.longitude+0.05,0.4,"8\$ per hour",
            "https://05a56bcbb6bd8e964f64-5f63a9a90be9c7873869b0beb9b45e3c.ssl.cf1.rackcdn.com/5YJSA1H1XEFP34954/9c702abaffc83a804232b802de6828bc.jpg"))
        MapLocationRepository.create(MapLocation("Also a car", LocationType.CAR,
            userLocation.latitude+0.07,
            userLocation.longitude-0.01,0.9,"1\$ per hour",
            "https://i.pinimg.com/originals/a3/40/53/a34053f7922b8cc610030e42d456a239.jpg"))
        MapLocationRepository.create(MapLocation("Station #42", LocationType.STATION,
            userLocation.latitude-0.05,
            userLocation.longitude-0.05,null,"8\$ per hour",
            "http://www.itsinternational.com/_resources/assets/inline/custom/18/72842.jpg"))
    }
}
