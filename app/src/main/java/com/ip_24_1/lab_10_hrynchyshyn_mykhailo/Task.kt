package com.ip_24_1.lab_10_hrynchyshyn_mykhailo

data class Task(
    val title: String,
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)