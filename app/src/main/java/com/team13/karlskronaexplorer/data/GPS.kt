package com.team13.karlskronaexplorer.data

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.location.LocationServices

// Code sources used to make this is https://www.youtube.com/watch?v=mwzKYIB9cQs and https://developer.android.com/develop/sensors-and-location/location
class GPS (context: Context, activity: Activity){
    private val appcontext = context
    private val locationClient = LocationServices.getFusedLocationProviderClient(appcontext)
    private val curActivity = activity

    //Gets location. Have not fully tested it
    @SuppressLint("MissingPermission")
    fun getLocation(onLocationReceived: (Location?) -> Unit){
        if(permissions()){
            if(gpsActivated()){
                locationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token).addOnSuccessListener(){ location ->
                    onLocationReceived(location)
                }.addOnFailureListener { exception ->
                    println("Failed to get location: ${exception.message}")
                    onLocationReceived(null)
                }
                // Should await this
            }
            else{
                //Activate GPS
            }
        }
        else{
            //Requests perm
            requestPerm()
        }
    }

    //Checks permission
    private fun permissions(): Boolean{
        if(ActivityCompat.checkSelfPermission(appcontext, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appcontext, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    //Requests permission
    private fun requestPerm(){
        ActivityCompat.requestPermissions(curActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
    }

    //Checks if gps is on
    private fun gpsActivated(): Boolean{
        val locationManager = appcontext.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}