package com.tsystems.r2b.dflow.screens.map

import android.Manifest
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
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
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


class MapFragment : Fragment(), LocationEngineListener {

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
                // return true to not show title
                false
            }
        }
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
            locationComponent.locationEngine?.addLocationEngineListener(this)
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
