package com.tsystems.r2b.dflow

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import com.squareup.picasso.Picasso
import com.tsystems.r2b.dflow.data.local.DFlowDb

class DFlowApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DFlowDb.init(this.applicationContext)
        Mapbox.getInstance(applicationContext, getString(R.string.access_token))

        val picasso = Picasso.Builder(this).build()
        if (BuildConfig.DEBUG) {
            picasso.setIndicatorsEnabled(true)
            picasso.isLoggingEnabled = true
        }
        Picasso.setSingletonInstance(picasso)
    }
}