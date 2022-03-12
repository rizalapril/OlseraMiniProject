package com.example.olseraminiproject.view

import android.os.Bundle
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView
import com.example.olseraminiproject.R
import com.example.olseraminiproject.base.BaseActivity
import com.example.olseraminiproject.util.CommonUtil
import com.example.olseraminiproject.view.customview.GoogleMapWithScrollFix
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.CameraPosition


class DetailsActivity : BaseActivity(), OnMapReadyCallback {

    var nestedLayout: NestedScrollView? = null
    var miniMap: GoogleMapWithScrollFix? = null

    var latitude = "0.0"
    var longitude = "0.0"

    var mMap: GoogleMap? = null
    var mCurrLocationMarker: Marker? = null

    override fun getLayoutResourceId(): Int = R.layout.activity_details

    override fun initView(savedInstanceState: Bundle?) {

        nestedLayout = findViewById<NestedScrollView>(R.id.nestedScrollView)
        miniMap = supportFragmentManager.findFragmentById(R.id.miniMap) as GoogleMapWithScrollFix
        miniMap?.getMapAsync(this)

    }

    override fun initListener() {
        miniMap?.setListener { //Here is the magic happens.
            //we disable scrolling of outside scroll view here
            nestedLayout?.requestDisallowInterceptTouchEvent(true)
        }
    }

    override fun initObserver() {
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (latitude!="0.0" && longitude!="0.0"){
            setMarkerMap()
        }
    }

    fun setMarkerMap(){
        val latLng = LatLng(latitude.toDouble(), longitude.toDouble())
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)

        mMap?.clear()
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        markerOptions.position
        mCurrLocationMarker = mMap?.addMarker(markerOptions)

        zoomToCurrent()
    }

    fun zoomToCurrent(){
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(latitude.toDouble(), longitude.toDouble()))
            .zoom(17f)
            .build()
        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

}