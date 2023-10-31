package com.shreyd.co2tracker
import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    //Unused Class
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception()
}