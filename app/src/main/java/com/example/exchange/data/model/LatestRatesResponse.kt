package com.example.exchange.data.model

data class LatestRatesResponse(
    val base: String,
    val rates: Map<String, Double>
)