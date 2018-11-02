package com.tsystems.r2b.dflow.screens.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tsystems.r2b.dflow.data.repository.MapLocationRepository
import com.tsystems.r2b.dflow.model.LocationType
import com.tsystems.r2b.dflow.model.MapLocation
import com.tsystems.r2b.dflow.util.ResourcesConst
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapViewModel : ViewModel() {
    val locations: LiveData<List<MapLocation>> = MapLocationRepository.getAll()
    var currentPosition: LatLng? = null
    val route: MutableLiveData<DirectionsRoute?> = MutableLiveData()

    // TODO: move mapbox api calls to separate file
    fun buildRouteTo(destination: LatLng) {
        currentPosition?.let { currPosition ->
            // Set up origin and destination coordinates for the call to the Mapbox Directions API
            val mockCurrentLocation = Point.fromLngLat(
                currPosition.longitude,
                currPosition.latitude
            )
            val destinationMarker = Point.fromLngLat(destination.longitude, destination.latitude)

            // Initialize the directionsApiClient object for eventually drawing a navigation route on the map
            val directionsApiClient = MapboxDirections.builder()
                .origin(mockCurrentLocation)
                .destination(destinationMarker)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(ResourcesConst.mapBoxToken)
                .build()

            directionsApiClient.enqueueCall(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    // Check that the response isn't null and that the response has a route
                    when {
                        response.body() == null -> Log.e(
                            this.javaClass.name,
                            "No routes found, make sure you set the right user and access token."
                        )
                        response.body()!!.routes().size < 1 -> Log.e(this.javaClass.name, "No routes found")
                        else -> {
                            val currentRoute = response.body()!!.routes()[0]
                            route.postValue(currentRoute)
                        }
                    }
                }

                override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                    Log.e(this.javaClass.name, "Directions API Failure")
                }
            })

        }
    }


    fun getNearbyLocations(userLocation: LatLng) {
        MapLocationRepository.create(
            MapLocation(
                "Jaguar E Pace", LocationType.CAR,
                userLocation.latitude + 0.02,
                userLocation.longitude + 0.01, 0.6, "5\$ per hour",
                "http://grantandgreen.de/wp-content/uploads/2018/03/Jag-E-Pace-BS-Blue.jpg"
            )
        )
        MapLocationRepository.create(
            MapLocation(
                "Tesla", LocationType.CAR,
                userLocation.latitude - 0.05,
                userLocation.longitude + 0.05, 0.4, "8\$ per hour",
                "https://05a56bcbb6bd8e964f64-5f63a9a90be9c7873869b0beb9b45e3c.ssl.cf1.rackcdn.com/5YJSA1H1XEFP34954/9c702abaffc83a804232b802de6828bc.jpg"
            )
        )
        MapLocationRepository.create(
            MapLocation(
                "Also a car", LocationType.CAR,
                userLocation.latitude + 0.07,
                userLocation.longitude - 0.01, 0.9, "1\$ per hour",
                "https://i.pinimg.com/originals/a3/40/53/a34053f7922b8cc610030e42d456a239.jpg"
            )
        )
        MapLocationRepository.create(
            MapLocation(
                "Station #42", LocationType.STATION,
                userLocation.latitude - 0.05,
                userLocation.longitude - 0.05, null, "8\$ per hour",
                "http://www.itsinternational.com/_resources/assets/inline/custom/18/72842.jpg"
            )
        )
    }
}
