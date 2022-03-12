package com.example.olseraminiproject.viewmodel

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.olseraminiproject.data.dataclass.CompanyDataClass
import com.example.olseraminiproject.data.local.entity.Company
import com.example.olseraminiproject.data.repository.CompanyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context,
    private val companyRepo: CompanyRepository
): ViewModel() {

    val resultAllList = MutableLiveData<List<CompanyDataClass>>()
    val resultActiveList = MutableLiveData<List<CompanyDataClass>>()
    val resultInactiveList = MutableLiveData<List<CompanyDataClass>>()

    fun loadData(){
        viewModelScope.launch(Dispatchers.IO) {
            val dataList = companyRepo.getLimitData(20, 0)
            if (dataList.size > 0){
                convertIntoAllList(dataList)
                convertIntoActiveList(dataList)
                convertIntoInactiveList(dataList)
            }else{

            }
        }
    }

    fun convertIntoAllList(list: List<Company>){
        var returnAllList = mutableListOf<CompanyDataClass>()
        var position = 1
        for (i in list){
            var data = CompanyDataClass()
            data.id = i.id ?: 0
            data.name = i.name
            data.address = i.address
            data.city = i.city
            data.postalcode = i.postalcode
            data.latitude = i.latitude
            data.longitude = i.longitude
            data.status = i.status

            if (!data.status) {
                data.txtStatus = "Inactive"
            }

            if (position.mod(2)==0){
                data.isMod = true
            }

            position++
            returnAllList.add(data)
        }

        resultAllList.postValue(returnAllList)
    }

    fun convertIntoActiveList(list: List<Company>){
        var returnAllList = mutableListOf<CompanyDataClass>()
        var position = 1
        for (i in list){
            if (i.status){
                var data = CompanyDataClass()
                data.id = i.id ?: 0
                data.name = i.name
                data.address = i.address
                data.city = i.city
                data.postalcode = i.postalcode
                data.latitude = i.latitude
                data.longitude = i.longitude
                data.status = i.status
                if (!data.status) {
                    data.txtStatus = "Inactive"
                }

                if (position.mod(2)==0){
                    data.isMod = true
                }

                position++
                returnAllList.add(data)
            }
        }
        resultActiveList.postValue(returnAllList)
    }

    fun convertIntoInactiveList(list: List<Company>){
        var returnAllList = mutableListOf<CompanyDataClass>()
        var position = 1
        for (i in list){
            if (!i.status){
                var data = CompanyDataClass()
                data.id = i.id ?: 0
                data.name = i.name
                data.address = i.address
                data.city = i.city
                data.postalcode = i.postalcode
                data.latitude = i.latitude
                data.longitude = i.longitude
                data.status = i.status
                if (!data.status) {
                    data.txtStatus = "Inactive"
                }

                if (position.mod(2)==0){
                    data.isMod = true
                }

                position++
                returnAllList.add(data)
            }
        }
        resultInactiveList.postValue(returnAllList)
    }

    fun getMoreAllData(limit: Int, offset: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val dataList = companyRepo.getLimitData(limit, offset)
            if (dataList.size > 0){
                convertIntoAllList(dataList)
            }else{
                var returnAllList = mutableListOf<CompanyDataClass>()
                resultAllList.postValue(returnAllList)
            }
        }
    }

    fun getMoreActiveData(limit: Int, offset: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val dataList = companyRepo.getLimitData(limit, offset)
            if (dataList.size > 0){
                convertIntoActiveList(dataList)
            }else{
                var returnAllList = mutableListOf<CompanyDataClass>()
                resultActiveList.postValue(returnAllList)
            }
        }
    }

    fun getMoreInactiveData(limit: Int, offset: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val dataList = companyRepo.getLimitData(limit, offset)
            if (dataList.size > 0){
                convertIntoInactiveList(dataList)
            }else{
                var returnAllList = mutableListOf<CompanyDataClass>()
                resultInactiveList.postValue(returnAllList)
            }
        }
    }
}