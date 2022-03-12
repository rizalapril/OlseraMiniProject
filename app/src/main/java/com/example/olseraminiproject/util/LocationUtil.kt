package com.example.olseraminiproject.util

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.olseraminiproject.data.dataclass.CustomLocation
import java.io.IOException
import java.lang.NullPointerException
import java.util.*


object LocationUtil {
    val TAG = "LocationUtil"
    fun getLocation(context: Context, latitude: Double, longitude: Double): CustomLocation?{
        var location: CustomLocation? = null
        Log.i(TAG, "getLocation: ${latitude}, ${longitude}")
        val geoCode = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geoCode.getFromLocation(latitude, longitude, 1)
            if (addresses.size > 0){
                val fullAddress = addresses.get(0).getAddressLine(0)
                val subLocal = addresses.get(0).subLocality
                val subCity = addresses.get(0).locality
                val city = addresses.get(0).subAdminArea
                val state = addresses.get(0).adminArea
                val country = addresses.get(0).countryName
                val postalCode = addresses.get(0).postalCode
                val knownName = addresses.get(0).featureName

                location = CustomLocation(fullAddress,subCity,city,state,country)
            }
        }catch (e: IOException){
            Log.i(TAG, "Err: ${e}")
        }catch (e: NullPointerException){
            Log.i(TAG, "Err: ${e}")
        }
        Log.i(TAG, "getLocation: location= ${location}")
        return location
    }

    fun getLastKnownLocation(context: Context): Pair<String, String> {
        var longitude = ""
        var latitude = ""
        if (ContextCompat.checkSelfPermission(context.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context.applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var lastLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation == null) {
                lastLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if(lastLocation != null){
                    longitude = "${lastLocation.longitude}"
                    latitude = "${lastLocation.latitude}"
                }
            }else{
                longitude = "${lastLocation.longitude}"
                latitude = "${lastLocation.latitude}"
            }
            Log.i(TAG,"(location getLastKnownLocation) latitude = ${latitude} and longitude = ${longitude}")
        }
        return Pair(latitude, longitude)
    }
}