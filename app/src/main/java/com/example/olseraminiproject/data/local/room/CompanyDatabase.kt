package com.example.olseraminiproject.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.olseraminiproject.data.local.dao.CompanyDao
import com.example.olseraminiproject.data.local.entity.Company

@Database(entities = arrayOf(Company::class), version = 1, exportSchema = false)
abstract class CompanyDatabase: RoomDatabase() {

    abstract fun accessDAO(): CompanyDao

    companion object {

        @Volatile
        private var INSTANCE: CompanyDatabase? = null

        fun getDBClient(context: Context): CompanyDatabase {
            if (INSTANCE != null) return INSTANCE as CompanyDatabase

            synchronized(this){
                INSTANCE = Room.databaseBuilder(context, CompanyDatabase::class.java, "COMPANY_DB")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE as CompanyDatabase
            }
        }
    }
}