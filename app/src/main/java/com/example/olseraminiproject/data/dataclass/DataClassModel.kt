package com.example.olseraminiproject.data.dataclass

data class CustomLocation(
    var fullAddress: String = "",
    var subCity: String = "",
    var city: String = "",
    var state: String = "",
    var country: String = "",
    var postalCode: String = ""
)

data class CompanyDataClass(
    var id: Int = 0,
    var name: String = "",
    var address: String = "",
    var city: String = "",
    var postalcode: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var status: Boolean = false
)