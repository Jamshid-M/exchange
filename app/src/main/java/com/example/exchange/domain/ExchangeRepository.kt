package com.example.exchange.domain

import com.example.exchange.data.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {

    suspend fun getExchangeRates(): List<ExchangeRate>
    val symbolFlow: Flow<Set<String>>
    suspend fun addSymbols(symbols: Set<String>)
    suspend fun loadLocalSymbols()
    suspend fun removeSymbol(symbol: String)
    suspend fun loadRemotePairs(): Map<String, String>
    suspend fun loadLocalExchangeRates(): List<ExchangeRate>
}