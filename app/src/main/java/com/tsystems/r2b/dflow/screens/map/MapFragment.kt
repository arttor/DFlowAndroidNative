package com.tsystems.r2b.dflow.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.tsystems.r2b.dflow.R
import com.tsystems.r2b.dflow.databinding.MapFragmentBinding
import com.tsystems.r2b.dflow.model.LocationType
import com.tsystems.r2b.dflow.util.Permissions
import kotlinx.android.synthetic.main.map_fragment.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapFragment : Fragment(), LocationEngineListener {
    private val NAVIGATION_LINE_WIDTH = 9f

    private val mapViewModel: MapViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(MapViewModel::class.java)
    }
    private val carIcon: Icon by lazy(LazyThreadSafetyMode.NONE) {
        IconFactory.getInstance(requireContext()).fromResource(R.drawable.ic_car)
    }
    private val stationIcon: Icon by lazy(LazyThreadSafetyMode.NONE) {
        IconFactory.getInstance(requireContext()).fromResource(R.drawable.ic_charging_station)
    }
    private val carSelectedIcon: Icon by lazy(LazyThreadSafetyMode.NONE) {
        IconFactory.getInstance(requireContext()).fromResource(R.drawable.ic_car_selected)
    }
    private val stationSelectedIcon: Icon by lazy(LazyThreadSafetyMode.NONE) {
        IconFactory.getInstance(requireContext()).fromResource(R.drawable.ic_charging_station_selected)
    }
    private val navigationLineColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        ContextCompat.getColor(requireContext(), R.color.colorPrimary)
    }

    private lateinit var mapboxMap: MapboxMap
    private var component: LocationComponent? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = MapFragmentBinding.inflate(inflater, container, false)
        binding.mapObjectsList.layoutManager = LinearLayoutManagerWithSmoothScroller(requireContext())
        val adapter = MapLocationsAdapter {
            mapboxMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.latitude,
                        it.longitude
                    ), 13.0
                )
            )
        }
        binding.mapObjectsList.adapter = adapter
        binding.setLifecycleOwner(this)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.mapObjectsList)
        subscribeUi(
            adapter,
            binding
        )
        return binding.root
    }

    private fun subscribeUi(adapter: MapLocationsAdapter, binding: MapFragmentBinding) {

        mapViewModel.locations.observe(viewLifecycleOwner, Observer {
            binding.hasMapLocations = (it != null && it.isNotEmpty())
        })

        mapViewModel.locations.observe(viewLifecycleOwner, Observer { mapLocations ->
            if (mapLocations != null && mapLocations.isNotEmpty())
                adapter.submitList(mapLocations)
            mapView.getMapAsync {
                for (marker in it.markers) {
                    it.removeMarker(marker)
                }
                for (location in mapLocations) {
                    it.addMarker(
                        MarkerOptions()
                            .position(location.latLng)
                            .title(location.name)
                            .icon(
                                when (location.type) {
                                    LocationType.CAR -> carIcon
                                    LocationType.STATION -> stationIcon
                                }
                            )
                    )
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            mapboxMap = it
            component = mapboxMap.locationComponent
            enableUserLocation(it)
        }
        setUpMarkerClickListener(map_objects_list)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        component?.locationEngine?.removeLocationEngineListener(this)
        component?.locationEngine?.removeLocationUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Permissions.LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mapView.getMapAsync {
                        enableUserLocation(it)
                    }
                } else {
                    activity?.longToast(R.string.user_location_permission_not_granted)
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun setUpMarkerClickListener(recyclerView: RecyclerView) {
        mapView.getMapAsync {
            it.setOnMarkerClickListener {
                val positionOfSelectedMarker = it.getPosition();

                mapboxMap.markers.forEachIndexed { index, marker ->
                    if (positionOfSelectedMarker == marker.position) {
                        recyclerView.smoothScrollToPosition(index)
                        // marker.icon
                    }
                }
                //setup icon change and route here
                getInformationFromDirectionsApi(positionOfSelectedMarker)
                // return true to not show title
                false
            }
        }
    }


    private fun getInformationFromDirectionsApi(
        destination: LatLng
    ) {
        if (mapViewModel.currentPosition == null) {
            requireActivity().toast(R.string.not_able_to_obtain_device_location)
        } else {
            val loc = mapViewModel.currentPosition as LatLng
            // Set up origin and destination coordinates for the call to the Mapbox Directions API
            val mockCurrentLocation = Point.fromLngLat(
                loc.longitude,
                loc.latitude
            )
            val destinationMarker = Point.fromLngLat(destination.longitude, destination.latitude)

            // Initialize the directionsApiClient object for eventually drawing a navigation route on the map
            val directionsApiClient = MapboxDirections.builder()
                .origin(mockCurrentLocation)
                .destination(destinationMarker)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build()

            directionsApiClient.enqueueCall(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    // Check that the response isn't null and that the response has a route
                    when {
                        response.body() == null -> Log.e(
                            "MapActivity",
                            "No routes found, make sure you set the right user and access token."
                        )
                        response.body()!!.routes().size < 1 -> Log.e("MapActivity", "No routes found")
                        else -> {
                            // Retrieve and draw the navigation route on the map
                            val currentRoute = response.body()!!.routes()[0]
                            drawNavigationPolylineRoute(currentRoute)
                        }
                    }
                }

                override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                    Log.e("MapActivity", "Failure")
                }
            })

        }
    }

    private fun drawNavigationPolylineRoute(route: DirectionsRoute) {
        // Check for and remove a previously-drawn navigation route polyline before drawing the new one
        if (mapboxMap.polylines.size > 0) {
            mapboxMap.removePolyline(mapboxMap.polylines[0])
        }

        // Convert LineString coordinates into a LatLng[]
        val lineString = LineString.fromPolyline(route.geometry()!!, Constants.PRECISION_6)
        val coordinates = lineString.coordinates()
        val polylineDirectionsPoints = arrayOfNulls<LatLng>(coordinates.size)
        for (i in coordinates.indices) {
            polylineDirectionsPoints[i] = LatLng(
                coordinates[i].latitude(),
                coordinates[i].longitude()
            )
        }

        // Draw the navigation route polyline on to the map
        mapboxMap.addPolyline(
            PolylineOptions()
                .add(*polylineDirectionsPoints)
                .color(navigationLineColor)
                .width(NAVIGATION_LINE_WIDTH)
        )
    }

    private fun enableUserLocation(mapboxMap: MapboxMap) {
        val locationComponent = mapboxMap.locationComponent
        // Activate with options
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Permissions.LOCATION
            )
        } else {
            locationComponent.activateLocationComponent(requireActivity())
            locationComponent.isLocationComponentEnabled = true
            locationComponent.locationEngine?.activate()
            locationComponent.locationEngine?.addLocationEngineListener(this)
            locationComponent.locationEngine?.requestLocationUpdates()
            locationComponent.lastKnownLocation?.let {
                val latLng = LatLng(
                    it.latitude,
                    it.longitude
                )
                mapboxMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng, 13.0
                    )
                )
                mapViewModel.currentPosition = latLng
                mapViewModel.getNearbyLocations(latLng)
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            mapViewModel.currentPosition = LatLng(
                it.latitude,
                it.longitude
            )
        }
    }

    override fun onConnected() {

    }
}
