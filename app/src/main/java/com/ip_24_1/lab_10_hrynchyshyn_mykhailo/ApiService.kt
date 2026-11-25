package com.ip_24_1.lab_10_hrynchyshyn_mykhailo

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("NBUStatService/v1/statdirectory/exchange?json")
    fun getExchangeRates(): Call<List<CurrencyRate>>

    companion object {
        private const val BASE_URL = "https://bank.gov.ua/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}