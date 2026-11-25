package com.ip_24_1.lab_10_hrynchyshyn_mykhailo

import com.google.gson.annotations.SerializedName

data class CurrencyRate(
    @SerializedName("txt") val name: String,
    @SerializedName("rate") val rate: Double,
    @SerializedName("cc") val code: String
)