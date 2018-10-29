package com.tsystems.r2b.dflow.screens.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.tsystems.r2b.dflow.R
import kotlinx.android.synthetic.main.map_fragment.*
import org.jetbrains.anko.longToast
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory




class MapFragment : Fragment(), PermissionsListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Mapbox.getInstance(this.requireContext(), getString(R.string.access_token))
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            enableUserLocation(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        activity?.longToast(R.string.user_location_permission_explanation)
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapView.getMapAsync {
                enableUserLocation(it)
            }
        } else {
            activity?.longToast(R.string.user_location_permission_not_granted)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableUserLocation(mapboxMap: MapboxMap) {
        val locationComponent = mapboxMap.locationComponent
        // Activate with options
        if (!PermissionsManager.areLocationPermissionsGranted(this.requireContext())) {
            val permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this.requireActivity())
        }else{
            locationComponent.activateLocationComponent(this.requireContext())
            // Enable to make component visible
            locationComponent.isLocationComponentEnabled = true
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
