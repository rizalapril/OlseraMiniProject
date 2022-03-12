package com.example.olseraminiproject.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtil {
    fun checkAllPermission(activity: Activity, callback: (Boolean) -> Unit){
        if (ContextCompat.checkSelfPermission(activity.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity.applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            callback(false)
        }else{
            callback(true)
        }
    }
}