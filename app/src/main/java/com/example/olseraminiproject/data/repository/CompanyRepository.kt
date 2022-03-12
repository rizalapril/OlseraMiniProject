package com.example.olseraminiproject.data.repository

import android.content.Context
import com.example.olseraminiproject.data.local.entity.Company
import com.example.olseraminiproject.data.local.room.CompanyDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface CompanyRepository {
    fun initializeDB(): CompanyDatabase
    fun getCompanyData(id: String): Company
    fun getAllData(): List<Company>
    fun insertData(name: String, address: String, city: String, postalcode: String, latitude: String, longitude: String, status: Boolean)
}
class CompanyRepositoryImpl(
    private val context: Context
): CompanyRepository{

    override fun initializeDB(): CompanyDatabase {
        return CompanyDatabase.getDBClient(context)
    }

    override fun insertData(name: String, address: String, city: String, postalcode: String, latitude: String, longitude: String, status: Boolean) {
        val db = initializeDB()
        CoroutineScope(Dispatchers.IO).launch {
            val companyData = Company(name, address, city, postalcode, latitude, longitude, status)
            db.accessDAO().insertData(companyData)
        }
    }

    override fun getAllData(): List<Company> {
        val db = initializeDB()
        val data = db.accessDAO().getAll()
        return data
    }

    override fun getCompanyData(id: String): Company {
        val db = initializeDB()
        val data = db.accessDAO().getCompany(id)
        return data
    }

}