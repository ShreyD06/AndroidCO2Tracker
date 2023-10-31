package com.shreyd.co2tracker
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

//Unused Class
class DefaultLocationClient(private val context: Context, private val client: FusedLocationProviderClient): LocationClient {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            if (!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Missing Location Permission")
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val GpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val NetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!GpsEnabled || !NetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

            val request = LocationRequest.Builder(interval)
            
        }
    }
}