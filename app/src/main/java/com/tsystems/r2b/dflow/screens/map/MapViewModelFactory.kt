package com.tsystems.r2b.dflow.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tsystems.r2b.dflow.data.repository.MapLocationRepository

/**
 * Factory that creates MapViewModel
 */
class MapViewModelFactory(private val mapLocationRepository: MapLocationRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = MapViewModel(mapLocationRepository) as T
}