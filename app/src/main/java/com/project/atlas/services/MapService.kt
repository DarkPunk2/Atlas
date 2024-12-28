package com.project.atlas.services

import android.Manifest
import android.app.Activity

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.project.atlas.models.Location
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MapService(private val activity: Activity) {

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    private fun hasLocationPermissions(): Boolean {
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val fineLocationPermission = ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        )
        return coarseLocationPermission == PackageManager.PERMISSION_GRANTED ||
                fineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    suspend fun getUserLocation(): Location? {
        return if (hasLocationPermissions()) {
            suspendCancellableCoroutine { continuation ->
                try {
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                val myLocation = Location(
                                    lat = location.latitude,
                                    lon = location.longitude,
                                    alias = "You",
                                    toponym = "You"
                                )
                                continuation.resume(myLocation)
                            } else {
                                continuation.resumeWithException(Exception("Location cannot be obtained"))
                            }
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                } catch (e: SecurityException) {
                    continuation.resumeWithException(e)
                }
            }
        } else {
            null
        }
    }
}


