package com.example.olseraminiproject.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import com.adevinta.leku.LocationPickerActivity
import com.example.olseraminiproject.R
import com.example.olseraminiproject.base.BaseActivity
import com.example.olseraminiproject.view.customview.GoogleMapWithScrollFix
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LONGITUDE
import androidx.lifecycle.lifecycleScope
import com.example.olseraminiproject.data.dataclass.CompanyDataClass
import com.example.olseraminiproject.util.LocationUtil
import com.example.olseraminiproject.viewmodel.DetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import android.view.MotionEvent





class DetailsActivity : BaseActivity(), OnMapReadyCallback {

    private val viewModel by viewModel<DetailsViewModel>()

    var btnClose: LinearLayout? = null
    var btnStatusActive: LinearLayout? = null
    var txtStatusActive: TextView? = null
    var btnStatusInactive: LinearLayout? = null
    var txtStatusInactive: TextView? = null

    var nestedLayout: NestedScrollView? = null
    var txtNameCompany: EditText? = null
    var txtAddress: EditText? = null
    var txtCity: EditText? = null
    var txtZipCode: EditText? = null

    var bgName: LinearLayout? = null
    var bgAddress: LinearLayout? = null
    var bgCity: LinearLayout? = null
    var bgZipCode: LinearLayout? = null

    var miniMap: GoogleMapWithScrollFix? = null
    var btnChangeLocation: FloatingActionButton? = null

    var deleteButton: LinearLayout? = null
    var btnCancel: LinearLayout? = null
    var btnSave: LinearLayout? = null

    var selectedStatus = true
    var latitude = "0.0"
    var longitude = "0.0"
    var isEditMode = false

    var mMap: GoogleMap? = null
    var mCurrLocationMarker: Marker? = null

    override fun getLayoutResourceId(): Int = R.layout.activity_details

    override fun initView(savedInstanceState: Bundle?) {
        isEditMode = intent.extras?.getBoolean("isedit") ?: false
        val id = intent.extras?.getInt("id") ?: 0
        latitude = intent.extras?.getString("latitude") ?: "0.0"
        longitude = intent.extras?.getString("longitude") ?: "0.0"

        btnClose = findViewById<LinearLayout>(R.id.btnClose)
        btnStatusActive = findViewById<LinearLayout>(R.id.btnStatusActive)
        txtStatusActive = findViewById<TextView>(R.id.txtStatusActive)
        btnStatusInactive = findViewById<LinearLayout>(R.id.btnStatusInactive)
        txtStatusInactive = findViewById<TextView>(R.id.txtStatusInactive)

        txtNameCompany = findViewById<EditText>(R.id.txtNameCompany)
        txtAddress = findViewById<EditText>(R.id.txtAddress)
        txtCity = findViewById<EditText>(R.id.txtCity)
        txtZipCode = findViewById<EditText>(R.id.txtZipCode)

        bgName = findViewById<LinearLayout>(R.id.bgName)
        bgAddress = findViewById<LinearLayout>(R.id.bgAddress)
        bgCity = findViewById<LinearLayout>(R.id.bgCity)
        bgZipCode = findViewById<LinearLayout>(R.id.bgZipCode)

        nestedLayout = findViewById<NestedScrollView>(R.id.nestedScrollView)
        btnChangeLocation = findViewById<FloatingActionButton>(R.id.btnChangeLocation)

        deleteButton = findViewById<LinearLayout>(R.id.deleteButton)
        btnCancel = findViewById<LinearLayout>(R.id.btnCancel)
        btnSave = findViewById<LinearLayout>(R.id.btnSave)

        miniMap = supportFragmentManager.findFragmentById(R.id.miniMap) as GoogleMapWithScrollFix
        miniMap?.getMapAsync(this)

        if (isEditMode){
            viewModel.loadData(id)
            deleteButton?.visibility = View.VISIBLE
        }
    }

    override fun initListener() {
        miniMap?.setListener {
            nestedLayout?.requestDisallowInterceptTouchEvent(true)
        }

        btnClose?.setOnClickListener { v ->
            close()
        }

        btnStatusActive?.setOnClickListener { v ->
            selectStatus(selectedStatus)
        }

        btnStatusInactive?.setOnClickListener { v ->
            selectStatus(selectedStatus)
        }

        btnChangeLocation?.setOnClickListener { v ->
            openMap()
        }

        btnCancel?.setOnClickListener { v ->
            close()
        }

        btnSave?.setOnClickListener { v ->
            saveData()
        }

        deleteButton?.setOnClickListener { v ->
            viewModel.deleteData()
            finish()
        }

        txtNameCompany?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                bgName?.background = ContextCompat.getDrawable(this, R.drawable.bg_focus_edittext)
            } else {
                bgName?.background = ContextCompat.getDrawable(this, R.drawable.bg_default_edittext)
            }
        }

        txtAddress?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                bgAddress?.background = ContextCompat.getDrawable(this, R.drawable.bg_focus_edittext)
            } else {
                bgAddress?.background = ContextCompat.getDrawable(this, R.drawable.bg_default_edittext)
            }
        }

        txtCity?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                bgCity?.background = ContextCompat.getDrawable(this, R.drawable.bg_focus_edittext)
            } else {
                bgCity?.background = ContextCompat.getDrawable(this, R.drawable.bg_default_edittext)
            }
        }

        bgZipCode?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                bgZipCode?.background = ContextCompat.getDrawable(this, R.drawable.bg_focus_edittext)
            } else {
                bgZipCode?.background = ContextCompat.getDrawable(this, R.drawable.bg_default_edittext)
            }
        }
    }

    override fun initObserver() {
        viewModel.resultData.observe(this, Observer { result ->
            txtNameCompany?.setText("${result.name}")
            txtAddress?.setText("${result.address}")
            txtCity?.setText("${result.city}")
            txtZipCode?.setText("${result.postalcode}")

            latitude = result.latitude
            longitude = result.longitude
            setMarkerMiniMap()

            if (!result.status){
                //inactive
                txtStatusActive?.setTextColor(ContextCompat.getColor(this, android.R.color.tab_indicator_text))
                txtStatusInactive?.setTextColor(ContextCompat.getColor(this, R.color.white))
                btnStatusActive?.background = ContextCompat.getDrawable(this, R.drawable.bg_left_status_default)
                btnStatusInactive?.background = ContextCompat.getDrawable(this, R.drawable.bg_right_status_blue)

                selectedStatus = false
            }else{
                //active
                txtStatusActive?.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtStatusInactive?.setTextColor(ContextCompat.getColor(this, android.R.color.tab_indicator_text))
                btnStatusActive?.background = ContextCompat.getDrawable(this, R.drawable.bg_left_status_blue)
                btnStatusInactive?.background = ContextCompat.getDrawable(this, R.drawable.bg_right_status_default)

                selectedStatus = true
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (latitude!="0.0" && longitude!="0.0"){
            setMarkerMiniMap()
        }
    }

    fun close(){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun selectStatus(select: Boolean){
        if (select){
            //inactive
            txtStatusActive?.setTextColor(ContextCompat.getColor(this, android.R.color.tab_indicator_text))
            txtStatusInactive?.setTextColor(ContextCompat.getColor(this, R.color.white))
            btnStatusActive?.background = ContextCompat.getDrawable(this, R.drawable.bg_left_status_default)
            btnStatusInactive?.background = ContextCompat.getDrawable(this, R.drawable.bg_right_status_blue)

            selectedStatus = false
        }else{
            //active
            txtStatusActive?.setTextColor(ContextCompat.getColor(this, R.color.white))
            txtStatusInactive?.setTextColor(ContextCompat.getColor(this, android.R.color.tab_indicator_text))
            btnStatusActive?.background = ContextCompat.getDrawable(this, R.drawable.bg_left_status_blue)
            btnStatusInactive?.background = ContextCompat.getDrawable(this, R.drawable.bg_right_status_default)

            selectedStatus = true
        }
    }

    fun saveData(){

        val name = txtNameCompany?.getText()?.toString() ?: ""
        val address = txtAddress?.getText()?.toString() ?: ""
        val city = txtCity?.getText()?.toString() ?: ""
        val postalcode = txtZipCode?.getText()?.toString() ?: ""

        viewModel.saveData(name, address, city, postalcode, latitude, longitude, selectedStatus)


        if (isEditMode){
            close()
        }else{
            clear()
        }
    }

    fun clear(){
        txtNameCompany?.setText("")
        txtAddress?.setText("")
        txtCity?.setText("")
        txtZipCode?.setText("")

        //set into active
        selectStatus(false)
    }

    fun setMarkerMiniMap(){
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

    fun setSelectedAddress(_lat: String, _long: String){
        lifecycleScope.launch(Dispatchers.IO){
            val addressLocation = LocationUtil.getLocation(this@DetailsActivity, _lat.toDouble(), _long.toDouble())
            addressLocation?.let {
                this@DetailsActivity.runOnUiThread {
                    txtAddress?.setText("${it.fullAddress}")
                    txtCity?.setText("${it.city}")
                    txtZipCode?.setText("${it.postalCode}")
                }
            }
        }
    }

    fun openMap(){

        val locationPickerIntent = LocationPickerActivity.Builder()
            .withLocation(latitude.toDouble(), longitude.toDouble())
            .withGeolocApiKey("AIzaSyCgLwhmLQM4jqiuuJve-ulBWWdtorqhJHw")
            .withGooglePlacesApiKey("AIzaSyCgLwhmLQM4jqiuuJve-ulBWWdtorqhJHw")
            .build(applicationContext)

        resultActivity.launch(locationPickerIntent)
    }


    var resultActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when(result.resultCode){
            Activity.RESULT_OK -> {
                val data: Intent? = result.data
                val _lat = data?.getDoubleExtra(LATITUDE, 0.0) ?: 0.0
                val _long = data?.getDoubleExtra(LONGITUDE, 0.0) ?: 0.0

                latitude = _lat.toString()
                longitude = _long.toString()
                setMarkerMiniMap()
                setSelectedAddress(latitude, longitude)
            }
            Activity.RESULT_CANCELED -> {

            } else -> {
        }
        }
    }

}