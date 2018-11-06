package com.tlabscloud.r2b.dflow.util

import android.content.Context
import com.tsystems.r2b.dflow.R

object PermissionsConst {
    const val LOCATION = 1
}
object ResourcesConst {
    lateinit var mapBoxToken: String

    fun init(context:Context){
        mapBoxToken = context.getString(R.string.access_token)
    }
}
