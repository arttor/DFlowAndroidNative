package com.tlabscloud.r2b.dflow

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import com.squareup.picasso.Picasso
import com.tlabscloud.r2b.dflow.util.ResourcesConst
import com.tlabscloud.r2b.dflow.BuildConfig
import com.tlabscloud.r2b.dflow.R

class DFlowApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(applicationContext, getString(R.string.access_token))

        val picasso = Picasso.Builder(this).build()
        if (BuildConfig.DEBUG) {
            picasso.setIndicatorsEnabled(true)
            picasso.isLoggingEnabled = true
        }
        Picasso.setSingletonInstance(picasso)
        ResourcesConst.init(applicationContext)
    }
}