package com.example.olseraminiproject.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.olseraminiproject.data.repository.CompanyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val context: Context,
    private val companyRepo: CompanyRepository
): ViewModel() {

    fun loadData(){

    }

    fun saveData(name: String, address: String, city: String, postalcode: String, latitude: String, longitude: String, status: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            companyRepo.insertData(name, address, city, postalcode, latitude, longitude, status)
        }
    }

    fun editData(){

    }
}