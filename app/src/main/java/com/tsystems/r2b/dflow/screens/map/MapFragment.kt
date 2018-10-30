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
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.tsystems.r2b.dflow.R
import com.tsystems.r2b.dflow.util.Permissions
import kotlinx.android.synthetic.main.map_fragment.*
import org.jetbrains.anko.longToast


class MapFragment : Fragment(), LocationEngineListener {
    override fun onLocationChanged(location: Location?) {

    }

    override fun onConnected() {

    }

    private lateinit var mapboxMap: MapboxMap
    private var component: LocationComponent? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            mapboxMap = it
            component = mapboxMap.locationComponent
            enableUserLocation(it)
        }
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

    private fun enableUserLocation(mapboxMap: MapboxMap) {
        val locationComponent = mapboxMap.locationComponent
        // Activate with options
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
//            ||
//            ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Permissions.LOCATION
            )
        } else {
            locationComponent.activateLocationComponent(requireActivity())
            // Enable to make component visible
            locationComponent.isLocationComponentEnabled = true
            locationComponent.locationEngine?.addLocationEngineListener(this)
            locationComponent.lastKnownLocation?.let {
                mapboxMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.latitude,
                            it.longitude
                        ), 13.0
                    )
                )
            }
        }
    }
}
