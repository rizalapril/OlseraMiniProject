package com.example.olseraminiproject.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.olseraminiproject.data.dataclass.CompanyDataClass
import com.example.olseraminiproject.data.repository.CompanyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val context: Context,
    private val companyRepo: CompanyRepository
): ViewModel() {
    
    val resultData = MutableLiveData<CompanyDataClass>()

    var isEditMode = false
    var tempId = 0

    fun loadData(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            tempId = id
            val data = companyRepo.getCompanyData(id)
            if (data!=null){
                var _data = CompanyDataClass()
                _data.id = data.id ?: 0
                _data.name = data.name
                _data.address = data.address
                _data.city = data.city
                _data.postalcode = data.postalcode
                _data.latitude = data.latitude
                _data.longitude = data.longitude
                _data.status = data.status

                resultData.postValue(_data)
            }
            isEditMode=true
        }
    }

    fun saveData(name: String, address: String, city: String, postalcode: String, latitude: String, longitude: String, status: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            if (isEditMode){
                companyRepo.editData(tempId, name, address, city, postalcode, latitude, longitude, status)
            }else{
                companyRepo.insertData(name, address, city, postalcode, latitude, longitude, status)
            }
        }
    }

    fun deleteData(){
        viewModelScope.launch(Dispatchers.IO) {
            companyRepo.deleteData(tempId)
        }
    }
}