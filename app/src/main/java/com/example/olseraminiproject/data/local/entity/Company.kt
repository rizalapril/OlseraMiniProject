package com.example.olseraminiproject.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Company(
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "address")
    var address: String,
    @ColumnInfo(name = "city")
    var city: String,
    @ColumnInfo(name = "postalcode")
    var postalcode: String,
    @ColumnInfo(name = "latitude")
    var latitude: String,
    @ColumnInfo(name = "longitude")
    var longitude: String,
    @ColumnInfo(name = "status")
    var status: Boolean
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null
}