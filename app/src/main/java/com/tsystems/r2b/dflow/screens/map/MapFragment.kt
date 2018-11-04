package com.tsystems.r2b.dflow.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.LineString
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.tsystems.r2b.dflow.R
import com.tsystems.r2b.dflow.databinding.MapFragmentBinding
import com.tsystems.r2b.dflow.model.LocationType
import com.tsystems.r2b.dflow.model.MapLocation
import com.tsystems.r2b.dflow.util.Injector
import com.tsystems.r2b.dflow.util.PermissionsConst
import com.tsystems.r2b.dflow.util.SnapOnScrollListener
import kotlinx.android.synthetic.main.map_fragment.*
import org.jetbrains.anko.longToast


class MapFragment : Fragment() {
    private val NAVIGATION_LINE_WIDTH = 9f

    private lateinit var mapViewModel: MapViewModel

    private val carIcon: Icon by lazy(LazyThreadSafetyMode.NONE) {
        IconFactory.getInstance(requireContext()).fromResource(R.drawable.ic_car)
    }
    private val stationIcon: Icon by lazy(LazyThreadSafetyMode.NONE) {
        IconFactory.getInstance(requireContext()).fromResource(R.drawable.ic_charging_station)
    }
    private val navigationLineColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        ContextCompat.getColor(requireContext(), R.color.colorPrimary)
    }

    private lateinit var mapBoxMap: MapboxMap
    private var component: LocationComponent? = null

    private val onLocationClickListener: (MapLocation) -> Unit = { location ->
        mapBoxMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), 13.0
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = MapFragmentBinding.inflate(inflater, container, false)

        val context = context ?: return binding.root
        val factory = Injector.getMapViewModelFactory(context)
        mapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel::class.java)

        binding.mapObjectsList.layoutManager = LinearLayoutManagerWithSmoothScroller(requireContext())
        val adapter = MapLocationsAdapter(onLocationClickListener)
        binding.mapObjectsList.adapter = adapter
        binding.setLifecycleOwner(this)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.mapObjectsList)
        val snapOnScrollListener = SnapOnScrollListener(snapHelper) {
            val ad = binding.mapObjectsList.adapter as MapLocationsAdapter
            val loc = ad.getItemByPosition(it)
            mapViewModel.buildRouteTo(loc.latLng)
        }
        binding.mapObjectsList.addOnScrollListener(snapOnScrollListener)
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

        mapViewModel.route.observe(viewLifecycleOwner, Observer { route ->
            route?.let {
                drawNavigationPolylineRoute(it)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            mapBoxMap = it
            component = mapBoxMap.locationComponent
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
        //component?.locationEngine?.removeLocationEngineListener(this)
        component?.locationEngine?.removeLocationUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionsConst.LOCATION -> {
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
        mapView.getMapAsync { map ->
            map.setOnMarkerClickListener {
                val positionOfSelectedMarker = it.position

                map.markers.forEachIndexed { index, marker ->
                    if (positionOfSelectedMarker == marker.position) {
                        recyclerView.smoothScrollToPosition(index)
                    }
                }
                mapViewModel.buildRouteTo(positionOfSelectedMarker)
                // return true to not show title
                false
            }
        }
    }

    private fun drawNavigationPolylineRoute(route: DirectionsRoute) {
        // Check for and remove a previously-drawn navigation route polyline before drawing the new one
        if (mapBoxMap.polylines.size > 0) {
            mapBoxMap.removePolyline(mapBoxMap.polylines[0])
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
        mapBoxMap.addPolyline(
            PolylineOptions()
                .add(*polylineDirectionsPoints)
                .color(navigationLineColor)
                .width(NAVIGATION_LINE_WIDTH)
        )

        val latLngBounds = LatLngBounds.Builder()
            .includes(polylineDirectionsPoints.toMutableList())
            .build()

        mapBoxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50, 50, 50, 700), 2000)
    }

    private fun enableUserLocation(mapboxMap: MapboxMap) {
        val locationComponent = mapboxMap.locationComponent
        // Activate with options
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PermissionsConst.LOCATION
            )
        } else {
            val engine = LocationEngineProvider(requireContext()).obtainBestLocationEngineAvailable()
            engine.priority = LocationEnginePriority.HIGH_ACCURACY
            // engine
            locationComponent.activateLocationComponent(requireActivity(), engine)
            locationComponent.isLocationComponentEnabled = true
            locationComponent.locationEngine?.addLocationEngineListener(object : LocationEngineListener {
                override fun onLocationChanged(location: Location?) {
                    location?.let {
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
                    locationComponent.locationEngine?.removeLocationEngineListener(this)
                }

                @SuppressLint("MissingPermission")
                override fun onConnected() {
                    locationComponent.locationEngine?.requestLocationUpdates()
                }
            })
            locationComponent.locationEngine?.activate()
        }
    }
}
