package com.tsystems.r2b.dflow.screens.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tsystems.r2b.dflow.data.repository.MapLocationRepository
import com.tsystems.r2b.dflow.model.MapLocation


class MapViewModel : ViewModel() {
    val locations: LiveData<List<MapLocation>> = MapLocationRepository.getAll()
}
