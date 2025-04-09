package com.example.exchange.data.remote

import com.example.exchange.BuildConfig
import com.example.exchange.data.model.LatestRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeService {

    @GET("currencies.json")
    suspend fun loadRemotePairs(@Query("app_id") key: String = BuildConfig.API_KEY): Map<String, String>

    @GET("latest.json")
    suspend fun loadLatestPairs(@Query("app_id") key: String = BuildConfig.API_KEY, @Query("symbols") symbols: String): LatestRatesResponse
}