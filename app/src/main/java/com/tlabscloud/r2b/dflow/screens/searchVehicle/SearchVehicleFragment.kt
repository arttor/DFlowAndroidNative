package com.tlabscloud.r2b.dflow.screens.searchVehicle

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
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
import com.tlabscloud.r2b.dflow.MainViewModel
import com.tlabscloud.r2b.dflow.R
import com.tlabscloud.r2b.dflow.databinding.SearchVehicleFragmentBinding
import com.tlabscloud.r2b.dflow.model.Vehicle
import com.tlabscloud.r2b.dflow.util.Injector
import com.tlabscloud.r2b.dflow.util.PermissionsConst
import com.tlabscloud.r2b.dflow.util.SnapOnScrollListener
import kotlinx.android.synthetic.main.search_vehicle_fragment.*
import org.jetbrains.anko.longToast


class SearchVehicleFragment : Fragment() {
    private val NAVIGATION_LINE_WIDTH = 9f

    private lateinit var currentContext: Context
    private lateinit var binding: SearchVehicleFragmentBinding

    private val searchVehicleViewModel: SearchVehicleViewModel by lazy(LazyThreadSafetyMode.NONE) {
        val factory = Injector.getMapViewModelFactory(currentContext)
        ViewModelProviders.of(this, factory).get(SearchVehicleViewModel::class.java)
    }

    private val mainViewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        val factory = Injector.getMapViewModelFactory(currentContext)
        ViewModelProviders.of(requireActivity(), factory).get(MainViewModel::class.java)
    }

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

    private val onLocationClickListener: (Vehicle) -> Unit = { location ->
        mapBoxMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), 13.0
            )
        )
    }

    private val bookVehicleListener: (Vehicle, ImageView) -> Unit = { location, imageView ->
        mainViewModel.vehicleToBook = location
        val extras = FragmentNavigatorExtras(
            imageView to getString(R.string.bookTransitionName)
        )
        Navigation.findNavController(requireActivity(), R.id.nav_fragment)
            .navigate(SearchVehicleFragmentDirections.actionBookVehicle(), extras)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        postponeEnterTransition()

        binding.searchVehiclesList.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchVehicleFragmentBinding.inflate(inflater, container, false)

        currentContext = context ?: return binding.root

        binding.searchVehiclesList.layoutManager =
                LinearLayoutManagerWithSmoothScroller(requireContext())
        val adapter = VehiclesListAdapter(
            onLocationClickListener,
            bookVehicleListener
        )
        binding.searchVehiclesList.adapter = adapter
        binding.setLifecycleOwner(this)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.searchVehiclesList)
        val snapOnScrollListener = SnapOnScrollListener(snapHelper) {
            val ad =
                binding.searchVehiclesList.adapter as VehiclesListAdapter
            val loc = ad.getItemByPosition(it)
            searchVehicleViewModel.buildRouteTo(loc.latLng)
        }
        binding.searchVehiclesList.addOnScrollListener(snapOnScrollListener)
        subscribeUi(
            adapter
        )
        val tr = TransitionInflater.from(context).inflateTransition(R.transition.move)
        sharedElementReturnTransition = tr
        return binding.root
    }

    private fun subscribeUi(adapter: VehiclesListAdapter) {

        searchVehicleViewModel.locations.observe(viewLifecycleOwner, Observer { mapLocations ->
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
                            .icon(carIcon)
                    )
                }
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
        setUpMarkerClickListener(search_vehicles_list)
        searchVehicleViewModel.route.observe(viewLifecycleOwner, Observer { route ->
            route?.let {
                drawNavigationPolylineRoute(it)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
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
                searchVehicleViewModel.buildRouteTo(positionOfSelectedMarker)
                // return true to not show title
                false
            }
        }
    }

    private fun drawNavigationPolylineRoute(route: DirectionsRoute) {
        // Check for and remove a previously-drawn navigation route polyline before drawing the new one
        mapView.getMapAsync { map ->

            if (map.polylines.size > 0) {
                map.removePolyline(map.polylines[0])
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
            map.addPolyline(
                PolylineOptions()
                    .add(*polylineDirectionsPoints)
                    .color(navigationLineColor)
                    .width(NAVIGATION_LINE_WIDTH)
            )

            val latLngBounds = LatLngBounds.Builder()
                .includes(polylineDirectionsPoints.toMutableList())
                .build()

            map.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50, 50, 50, 700), 2000)
        }
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
            if (isGeoLocationDisabled()) {
                // TODO: switch to using GP services like in Google or Yandex Maps instead of redirecting to settings
                AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setMessage(R.string.gps_disabled)
                    .setPositiveButton(
                        R.string.enable
                    ) { _, _ -> startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                    .setNegativeButton(R.string.disable) { dialog, _ -> dialog.cancel() }
                    .create()
                    .show()
            }

            val engine = LocationEngineProvider(requireContext()).obtainBestLocationEngineAvailable()
            engine.priority = LocationEnginePriority.HIGH_ACCURACY
            // check position every 5 sec
            engine.interval = 5000
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
                        if (searchVehicleViewModel.route.value == null)
                            mapboxMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng, 13.0
                                )
                            )
                        searchVehicleViewModel.getNearbyLocations(latLng)

                        searchVehicleViewModel.currentPosition = latLng
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

    private fun isGeoLocationDisabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
