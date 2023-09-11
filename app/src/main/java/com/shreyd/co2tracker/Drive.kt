package com.shreyd.co2tracker

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.Exclude
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Drive(
    @get:Exclude
    var id: String?,
    var startLoc: List<Double?>,
    var endLoc: List<Double?>,
    var startTime: Long?,
    var endTime: Long?,
    var waypoints: List<List<Double?>>? = null
//    var emission: Double
    )
{

}