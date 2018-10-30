package com.tsystems.r2b.dflow

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import com.tsystems.r2b.dflow.data.local.DFlowDb

class DFlowApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DFlowDb.init(this.applicationContext)
        Mapbox.getInstance(applicationContext, getString(R.string.access_token))    }
}