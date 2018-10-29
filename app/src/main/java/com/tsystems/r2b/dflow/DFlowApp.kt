package com.tsystems.r2b.dflow

import android.app.Application
import com.tsystems.r2b.dflow.data.local.DFlowDb

class DFlowApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DFlowDb.init(this.applicationContext)
    }
}