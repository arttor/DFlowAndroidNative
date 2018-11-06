package com.tlabscloud.r2b.dflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class User(
        @PrimaryKey var name: String = "",
        var lastLogin: Date = Date()
)