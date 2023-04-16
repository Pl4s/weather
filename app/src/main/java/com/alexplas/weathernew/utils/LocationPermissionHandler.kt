package com.alexplas.weathernew.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import javax.inject.Inject

class LocationPermissionHandler @Inject constructor(private val fragment: Fragment) {

    private val TAG = "LocationPermissionHandler"

    private val activity: Activity
        get() = fragment.requireActivity()

    private val locationPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true
            ) {
                checkLocationServices()
            } else {
                // Permission denied, handle it here or notify the user
            }
        }

    fun checkAndRequestPermission() {
        Log.d(TAG, "Checking location permissions")
        when {
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "Location permissions granted")
                // If the GPS is not enabled on the device, the GoogleApiAvailability dialog is shown to the user, which prompts them to enable GPS.
                checkLocationServices()
            }
            fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) -> {
                Log.d(TAG, "Showing location permission rationale")
                // Explain why the app needs location access and ask for permission
                // You can use a DialogFragment to show the explanation
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            }
            else -> {
                Log.d(TAG, "Requesting location permissions")
                // Request the permissions
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            }
        }
    }

    private val locationSettingsLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // GPS was enabled by the user
                Toast.makeText(activity, "GPS is enabled", Toast.LENGTH_SHORT).show()
            } else {
                // GPS was not enabled by the user
                Toast.makeText(activity, "GPS is not enabled", Toast.LENGTH_SHORT).show()
            }
        }

    @SuppressLint("MissingPermission")
    private fun checkLocationServices() {
        Log.d(TAG, "checkLocationServices() called") // Add this log
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0L)
            .build()

        val locationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()

        val client: SettingsClient = LocationServices.getSettingsClient(activity)

        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(locationSettingsRequest)

        task.addOnSuccessListener { response -> // Invoked if GPS is ON
            Log.d(TAG, "addOnSuccessListener() called") // Add this log
            val states = response.locationSettingsStates
            if (states?.isLocationPresent == true) { //Location settings are satisfied.
                Toast.makeText(
                    activity, "Device is ready to get the location",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Log.d(TAG, "Location settings are not satisfied")
                // Handle the case where location settings are not satisfied
            }
        }
        task.addOnFailureListener { e: Exception -> // Invoked if GPS is disabled/off
            Log.d(TAG, "addOnFailureListener() called") // Add this log
            if (e is ResolvableApiException) {
                val statusCode = e.statusCode
                if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        // Location settings are not satisfied.
                        // But could be fixed by showing the user a dialog.
                        val rae = e
                        locationSettingsLauncher.launch(IntentSenderRequest.Builder(rae.resolution.intentSender).build())
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Log.d(TAG, "Failed to show the user a dialog")
                        // Failed to show the user a dialog
                        Toast.makeText(
                            activity,
                            "Failed to show the user a dialog",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Log.d(TAG, "Some other type of error occurred")
                    // Some other type of error occurred
                    Toast.makeText(activity, "Some other type of error occurred", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Log.d(TAG, "Some other type of error occurred")
                // Some other type of error occurred
                Toast.makeText(activity, "Some other type of error occurred", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

}