package com.tlabscloud.r2b.dflow.screens.searchVehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tlabscloud.r2b.dflow.data.repository.VehicleRepository

/**
 * Factory that creates SearchVehicleViewModel
 */
class SearchVehicleViewModelFactory(private val vehicleRepository: VehicleRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = SearchVehicleViewModel(
        vehicleRepository
    ) as T
}