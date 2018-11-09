package com.tlabscloud.r2b.dflow.screens.searchVehicle

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
import com.tlabscloud.r2b.dflow.data.repository.VehicleRepository
import com.tlabscloud.r2b.dflow.model.Vehicle
import com.tlabscloud.r2b.dflow.model.VehicleTariff
import com.tlabscloud.r2b.dflow.util.ResourcesConst
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchVehicleViewModel constructor(private val vehicleRepository: VehicleRepository) : ViewModel() {
    val locations: LiveData<List<Vehicle>> = vehicleRepository.getAll()
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
                            this::javaClass.name,
                            "No routes found, make sure you set the right user and access token."
                        )
                        response.body()!!.routes().size < 1 -> Log.e(this::javaClass.name, "No routes found")
                        else -> {
                            val currentRoute = response.body()!!.routes()[0]
                            route.postValue(currentRoute)
                        }
                    }
                }

                override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                    Log.e(this::javaClass.name, "Directions API Failure")
                }
            })

        }
    }

    fun getNearbyLocations(userLocation: LatLng) {
        vehicleRepository.create(
            Vehicle(
                "1",
                "Jaguar E Pace",
                userLocation.latitude + 0.02,
                userLocation.longitude + 0.01,
                "http://grantandgreen.de/wp-content/uploads/2018/03/Jag-E-Pace-BS-Blue.jpg",
                0.6,
                200,
            5,
            VehicleTariff(
                "EUR",
                0.5,
                20
            ),
            "a789de"


            )
        )
        vehicleRepository.create(
            Vehicle(
                "2",
                "Tesla",
                userLocation.latitude - 0.05,
                userLocation.longitude + 0.05,
                "https://05a56bcbb6bd8e964f64-5f63a9a90be9c7873869b0beb9b45e3c.ssl.cf1.rackcdn.com/5YJSA1H1XEFP34954/9c702abaffc83a804232b802de6828bc.jpg",
                0.4,
                300,
                5,
                VehicleTariff(
                    "EUR",
                    0.4,
                    20
                ),
                "w123de"
            )
        )
        vehicleRepository.create(
            Vehicle(
                "3",
                "Also a car",
                userLocation.latitude+ 0.07,
                userLocation.longitude - 0.01,
                "https://i.pinimg.com/originals/a3/40/53/a34053f7922b8cc610030e42d456a239.jpg",
                0.9,
                100,
                2,
                VehicleTariff(
                    "EUR",
                    0.1,
                    30
                ),
                "s453de"
            )
        )
    }
}
