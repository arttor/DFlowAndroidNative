package com.tlabscloud.r2b.dflow.util

import androidx.lifecycle.LiveData
import com.tlabscloud.r2b.dflow.model.Vehicle

interface LocationsFilter {
    fun clean()
    fun applyType(type: String?)
    fun applyMinCharge(minCharge: Double?)
    fun applyMaxCost(maxCost: Double?)
    fun filer(vehicle: Vehicle): Boolean
}

class LocationsFilterLiveData : LiveData<LocationsFilter>(), LocationsFilter {
    private var type: String? = null
    private var minCharge: Double? = null
    private var maxCost: Double? = null
    override fun clean() {
        type = null
        minCharge = null
        maxCost = null
    }

    override fun applyType(type: String?) {
        if (this.type!=type){
            this.type = type
            this.postValue(this)
        }
    }

    override fun applyMinCharge(minCharge: Double?) {
        if (this.minCharge!=minCharge){
            this.minCharge = minCharge
            this.postValue(this)
        }
    }

    override fun applyMaxCost(maxCost: Double?) {
        if (this.maxCost!=maxCost){
            this.maxCost = maxCost
            this.postValue(this)
        }
    }

    override fun filer(vehicle: Vehicle): Boolean =
        when {
            vehicle.charge ?: Double.MAX_VALUE  < minCharge ?: Double.MIN_VALUE -> false
            else -> true
        }

}