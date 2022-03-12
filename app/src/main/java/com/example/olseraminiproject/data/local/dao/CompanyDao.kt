package com.example.olseraminiproject.data.local.dao

import androidx.room.*
import com.example.olseraminiproject.data.local.entity.Company

@Dao
interface CompanyDao {
    @Query("SELECT * FROM company")
    fun getAll(): List<Company>

    @Query("SELECT * FROM company LIMIT :limit OFFSET :offset")
    fun getCompanyList(limit: Int, offset: Int): List<Company>

    @Query("SELECT * FROM company WHERE id=:companyId")
    fun getCompany(companyId: String): Company

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(company: Company)

    @Delete
    fun delete(company: Company)
}