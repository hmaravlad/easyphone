package com.example.easyphone.data.providers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.easyphone.data.utils.DataProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class Coordiantes(val context: Context): DataProvider {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val onStopChannel = Channel<Location>()

    private var job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun onStop(value: Location) {
        Log.d("MY_DEBUG", "value: $value")
        onStopChannel.offer(value)
        onStopChannel.close()
    }

    override suspend fun getData(): String {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return "{no permission for geolocation}"
        }

        Log.d("MY_DEBUG", "getting location")

        val cancellationToken = CancellationTokenSource().token
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationToken).addOnSuccessListener { location : Location? ->
            Log.d("MY_DEBUG", "location: $location")
            if (location != null) onStop(location)
        }

        return withContext(Dispatchers.IO) {
            val value = onStopChannel.receive()
            val formatted = "latitude: ${value.latitude}, longitude: ${value.longitude}"
            return@withContext formatted
        }
    }
}