package com.example.olseraminiproject.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.olseraminiproject.R
import com.example.olseraminiproject.base.BaseActivity
import com.example.olseraminiproject.util.Constants
import com.example.olseraminiproject.util.PermissionUtil
import com.example.olseraminiproject.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel by viewModel<MainViewModel>()

    var btnAddNew: FloatingActionButton? = null

    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationManager: LocationManager? = null
    var lastLocation: Location? = null
    var latitude = "0.0"
    var longitude = "0.0"

    override fun getLayoutResourceId(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {

        btnAddNew = findViewById<FloatingActionButton>(R.id.btnAddNew)

        getPermission()

        viewModel.loadData()
    }

    override fun initListener() {
        btnAddNew?.setOnClickListener { v->
            openDetails()
        }
    }

    override fun initObserver() {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun getPermission(){
        PermissionUtil.checkAllPermission(this){
            if (it){
                //all permission is granted
                getLastKnownLocation()
            }else{
                //open dialog permission
                openDialogRequestPermission(this)
            }
        }
    }

    fun openDialogRequestPermission(activity: Activity){
        ActivityCompat.requestPermissions(activity, arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION), Constants.Permission_All)
    }

    fun getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient?.lastLocation?.addOnSuccessListener { location->
                if(location != null){
                    longitude = "${location.longitude}"
                    latitude = "${location.latitude}"
                }
            }
        }
    }

    fun openDetails(){
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        resultActivity.launch(intent)
    }

    var resultActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when(result.resultCode){
            Activity.RESULT_OK -> {
                val data: Intent? = result.data
            }
            Activity.RESULT_CANCELED -> {

            } else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.Permission_All){
            if (grantResults.size > 0){

                var message = ""

                for (i in 0..grantResults.size-1){
                    if (i == 0){
                        //skip to 1 for location check permission, because 0 and 1 is location permission request
                    }else if (i == 1){
                        //check location permission
                        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            fusedLocationClient?.lastLocation?.addOnSuccessListener { location->
                                if(location != null){
                                    longitude = "${location.longitude}"
                                    latitude = "${location.latitude}"
                                }
                            }
                        } else{
                            message = "location"
                        }
                    }
                }

                if (!message.isNullOrEmpty()){
//                    showNotification("Sorry, it appears you dont accept request permission. Please accept it", Constants.TagNotification_Warning)
                }
            }
        }else if(requestCode == Constants.Permission_Location){
            if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient?.lastLocation?.addOnSuccessListener { location->
                    if(location != null){
                        longitude = "${location.longitude}"
                        latitude = "${location.latitude}"
                    }
                }
            }else{
//                showNotification("Sorry, it appears you dont accept request permission. Please accept it", Constants.TagNotification_Warning)
            }
        }
    }
}