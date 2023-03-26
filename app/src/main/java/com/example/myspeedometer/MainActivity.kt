package com.example.myspeedometer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    // Location-related fields
    private lateinit var locationManager: LocationManager
    private var isTracking = false
    private var lastLocation: Location? = null
    private var distanceTravelled = 0.0

    // UI-related fields
    private lateinit var speedTextView: TextView
    private lateinit var maxSpeedTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var toggleButton: Button

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        speedTextView = findViewById(R.id.speedTextView)
        distanceTextView = findViewById(R.id.distanceTextView)
        toggleButton = findViewById(R.id.toggleButton)
        toggleButton.setOnClickListener { toggleTracking() }

        // Initialize location manager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun toggleTracking() {
        isTracking = !isTracking
        if (isTracking) {
            // Start tracking
            lastLocation = null
            distanceTravelled = 0.0
            toggleButton.text = getString(R.string.stop_tracking)
        } else {
            // Stop tracking
            toggleButton.text = getString(R.string.start_tracking)
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val speed = location.speed * 3.6 // convert m/s to km/h
            var maxSpeed = 0.0
            speedTextView.text = String.format("%.1f km/h", speed)

            if (isTracking) {
                if (lastLocation != null) {
                    // Calculate distance travelled
                    val distanceInMeters = location.distanceTo(lastLocation)
                    distanceTravelled += distanceInMeters / 1000 // convert meters to kilometers
                    distanceTextView.text = String.format("%.3f km", distanceTravelled)
                }
                lastLocation = location
            }

        }



        override fun onProviderEnabled(provider: String) {
            // Called when the provider is enabled by the user.
        }

        override fun onProviderDisabled(provider: String) {
            // Called when the provider is disabled by the user.
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            // Called when the provider status changes.
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                }
            }
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
    }
}

