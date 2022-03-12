package com.example.olseraminiproject.data.local.dao

import androidx.room.*
import com.example.olseraminiproject.data.local.entity.Company

@Dao
interface CompanyDao {
    @Query("SELECT * FROM company")
    fun getAll(): List<Company>

    @Query("SELECT * FROM company ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getCompanyList(limit: Int, offset: Int): List<Company>

    @Query("SELECT * FROM company WHERE status = 1 ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getCompanyActiveList(limit: Int, offset: Int): List<Company>

    @Query("SELECT * FROM company WHERE status = 0 ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getCompanyInactiveList(limit: Int, offset: Int): List<Company>

    @Query("SELECT * FROM company WHERE id=:companyId")
    fun getCompany(companyId: Int): Company

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(company: Company)

    @Query("UPDATE company SET name = :name, address = :address, city = :city, postalcode = :postalcode, latitude = :latitude, longitude = :longitude, status = :status WHERE id = :companyId")
    suspend fun updateData(companyId: Int, name: String, address: String, city: String, postalcode: String, latitude: String, longitude: String, status: Boolean)

    @Query("DELETE FROM company WHERE id = :companyId")
    fun delete(companyId: Int)
}