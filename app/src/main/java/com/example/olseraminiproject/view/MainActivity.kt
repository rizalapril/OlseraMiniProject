package com.example.olseraminiproject.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.olseraminiproject.R
import com.example.olseraminiproject.base.BaseActivity
import com.example.olseraminiproject.data.dataclass.CompanyDataClass
import com.example.olseraminiproject.util.Constants
import com.example.olseraminiproject.util.PermissionUtil
import com.example.olseraminiproject.view.adapter.ActiveStatusAdapter
import com.example.olseraminiproject.view.adapter.AllStatusAdapter
import com.example.olseraminiproject.view.adapter.InactiveStatusAdapter
import com.example.olseraminiproject.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel by viewModel<MainViewModel>()

    private lateinit var allStatusAdapter: AllStatusAdapter
    private lateinit var activeStatusAdapter: ActiveStatusAdapter
    private lateinit var inactiveStatusAdapter: InactiveStatusAdapter

    var vfMainPage: ViewFlipper? = null
    var btnAddNew: FloatingActionButton? = null
    var btnAllStatus: LinearLayout? = null
    var btnActiveStatus: LinearLayout? = null
    var btnInactiveStatus: LinearLayout? = null

    var txtFilterAllStatus: TextView? = null
    var txtFilterActiveStatus: TextView? = null
    var txtFilterInactiveStatus: TextView? = null

    var txtCountFilterAllStatus: TextView? = null
    var txtCountFilterActiveStatus: TextView? = null
    var txtCountFilterInactiveStatus: TextView? = null

    var recycleAllStatus: RecyclerView? = null
    var progressBarAllStatus: ProgressBar? = null
    var recycleActiveStatus: RecyclerView? = null
    var progressBarActiveStatus: ProgressBar? = null
    var recycleInactiveStatus: RecyclerView? = null
    var progressBarInactiveStatus: ProgressBar? = null

    var fusedLocationClient: FusedLocationProviderClient? = null
    var latitude = "0.0"
    var longitude = "0.0"
    private var isLoading = false

    override fun getLayoutResourceId(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        vfMainPage = findViewById<ViewFlipper>(R.id.vfMainPage)
        btnAddNew = findViewById<FloatingActionButton>(R.id.btnAddNew)
        btnAllStatus = findViewById<LinearLayout>(R.id.btnAllStatus)
        btnActiveStatus = findViewById<LinearLayout>(R.id.btnActiveStatus)
        btnInactiveStatus = findViewById<LinearLayout>(R.id.btnInactiveStatus)

        txtFilterAllStatus = findViewById<TextView>(R.id.txtFilterAllStatus)
        txtFilterActiveStatus = findViewById<TextView>(R.id.txtFilterActiveStatus)
        txtFilterInactiveStatus = findViewById<TextView>(R.id.txtFilterInactiveStatus)

        txtCountFilterAllStatus = findViewById<TextView>(R.id.txtCountFilterAllStatus)
        txtCountFilterActiveStatus = findViewById<TextView>(R.id.txtCountFilterActiveStatus)
        txtCountFilterInactiveStatus = findViewById<TextView>(R.id.txtCountFilterInactiveStatus)

        recycleAllStatus = findViewById<RecyclerView>(R.id.recycleAllStatus)
        progressBarAllStatus = findViewById<ProgressBar>(R.id.progressBarAllStatus)
        recycleActiveStatus = findViewById<RecyclerView>(R.id.recycleActiveStatus)
        progressBarActiveStatus = findViewById<ProgressBar>(R.id.progressBarActiveStatus)
        recycleInactiveStatus = findViewById<RecyclerView>(R.id.recycleInactiveStatus)
        progressBarInactiveStatus = findViewById<ProgressBar>(R.id.progressBarInactiveStatus)

        //adapter
        allStatusAdapter = AllStatusAdapter(applicationContext, this)
        recycleAllStatus?.adapter = allStatusAdapter
        activeStatusAdapter = ActiveStatusAdapter(applicationContext, this)
        recycleActiveStatus?.adapter = activeStatusAdapter
        inactiveStatusAdapter = InactiveStatusAdapter(applicationContext, this)
        recycleInactiveStatus?.adapter = inactiveStatusAdapter

        recycleAllStatus?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycleActiveStatus?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycleInactiveStatus?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        getPermission()

        viewModel.loadData()
        selectedView(1)
    }

    override fun initListener() {
        btnAddNew?.setOnClickListener { v->
            openDetails()
        }

        btnAllStatus?.setOnClickListener { v->
            selectedView(1)
        }
        btnActiveStatus?.setOnClickListener { v->
            selectedView(2)
        }
        btnInactiveStatus?.setOnClickListener { v->
            selectedView(3)
        }

        recycleAllStatus?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (!isLoading){
                        getMoreAllStatus(false)
                    }
                }
            }
        })

        recycleActiveStatus?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (!isLoading){
                        getMoreActiveStatus(false)
                    }
                }
            }
        })

        recycleInactiveStatus?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (!isLoading){
                        getMoreInactiveStatus(false)
                    }
                }
            }
        })
    }

    override fun initObserver() {
        viewModel.resultAllList.observe(this, Observer { result ->
            isLoading = false
            progressBarAllStatus?.visibility = View.GONE
            if (result.size > 0) {
                allStatusAdapter.addList(result as ArrayList<CompanyDataClass>)
            }else{
            }
            txtCountFilterAllStatus?.text = "(${allStatusAdapter.itemCount})"

        })

        viewModel.resultActiveList.observe(this, Observer { result ->
            isLoading = false
            progressBarActiveStatus?.visibility = View.GONE
            if (result.size > 0) {
                activeStatusAdapter?.addList(result as ArrayList<CompanyDataClass>)
            }else{
            }
            txtCountFilterActiveStatus?.text = "(${activeStatusAdapter.itemCount})"

        })

        viewModel.resultInactiveList.observe(this, Observer { result ->
            isLoading = false
            progressBarInactiveStatus?.visibility = View.GONE
            if (result.size > 0) {
                inactiveStatusAdapter?.addList(result as ArrayList<CompanyDataClass>)
            }else{
            }
            txtCountFilterInactiveStatus?.text = "(${inactiveStatusAdapter.itemCount})"

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        viewModel.loadData()
        allStatusAdapter?.clear()
        activeStatusAdapter?.clear()
        inactiveStatusAdapter?.clear()

        super.onResume()
    }

    fun selectedView(code: Int){
        txtFilterAllStatus?.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtFilterActiveStatus?.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtFilterInactiveStatus?.setTextColor(ContextCompat.getColor(this, R.color.black))

        txtCountFilterAllStatus?.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtCountFilterActiveStatus?.setTextColor(ContextCompat.getColor(this, R.color.black))
        txtCountFilterInactiveStatus?.setTextColor(ContextCompat.getColor(this, R.color.black))

        btnAllStatus?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        btnActiveStatus?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        btnInactiveStatus?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        when(code){
            1 ->{
                txtFilterAllStatus?.setTextColor(ContextCompat.getColor(this, R.color.green))
                txtCountFilterAllStatus?.setTextColor(ContextCompat.getColor(this, R.color.green))
                btnAllStatus?.background = ContextCompat.getDrawable(this, R.drawable.bg_round_selected_filter_left)
                vfMainPage?.displayedChild = 0
            }
            2 ->{
                txtFilterActiveStatus?.setTextColor(ContextCompat.getColor(this, R.color.green))
                txtCountFilterActiveStatus?.setTextColor(ContextCompat.getColor(this, R.color.green))
                btnActiveStatus?.setBackgroundColor(ContextCompat.getColor(this, R.color.teal))
                vfMainPage?.displayedChild = 1
            }
            3 ->{
                txtFilterInactiveStatus?.setTextColor(ContextCompat.getColor(this, R.color.green))
                txtCountFilterInactiveStatus?.setTextColor(ContextCompat.getColor(this, R.color.green))
                btnInactiveStatus?.background = ContextCompat.getDrawable(this, R.drawable.bg_round_selected_filter_right)
                vfMainPage?.displayedChild = 2
            }
        }
    }

    private fun getMoreAllStatus(isOnRefresh: Boolean) {
        isLoading = true

        if (!isOnRefresh){
            progressBarAllStatus?.visibility = View.VISIBLE
        }

        val offset = allStatusAdapter.itemCount

        val limit = 15
        lifecycleScope.launch(Dispatchers.IO){
            viewModel.getMoreAllData(limit, offset)
        }
    }

    private fun getMoreActiveStatus(isOnRefresh: Boolean) {
        isLoading = true

        if (!isOnRefresh){
            progressBarActiveStatus?.visibility = View.VISIBLE
        }

        val offset = activeStatusAdapter.itemCount

        val limit = 15
        lifecycleScope.launch(Dispatchers.IO){
            viewModel.getMoreActiveData(limit, offset)
        }
    }

    private fun getMoreInactiveStatus(isOnRefresh: Boolean) {
        isLoading = true

        if (!isOnRefresh){
            progressBarInactiveStatus?.visibility = View.VISIBLE
        }

        val offset = inactiveStatusAdapter.itemCount

        val limit = 15
        lifecycleScope.launch(Dispatchers.IO){
            viewModel.getMoreInactiveData(limit, offset)
        }
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

    fun editCompany(id: Int){
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("isedit", true)
        intent.putExtra("id", id)
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