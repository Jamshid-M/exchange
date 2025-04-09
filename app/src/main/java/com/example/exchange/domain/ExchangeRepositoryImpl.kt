package com.example.exchange.domain

import android.content.SharedPreferences
import android.text.TextUtils
import com.example.exchange.data.local.ExchangeDao
import com.example.exchange.data.local.ExchangeEntity
import com.example.exchange.data.model.ExchangeRate
import com.example.exchange.data.remote.ExchangeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val service: ExchangeService,
    private val dao: ExchangeDao,
    private val preferences: SharedPreferences
) : ExchangeRepository {
    private val SYMBOLS_KEY = "symbols"
    private val _symbolFlow = Channel<Set<String>>()

    override val symbolFlow = _symbolFlow.receiveAsFlow()

    override suspend fun getExchangeRates(): List<ExchangeRate> {
        val symbols = preferences.getStringSet(SYMBOLS_KEY, mutableSetOf())!!
        if (symbols.isEmpty()) {
            return emptyList()
        }
        val response = service.loadLatestPairs(symbols = TextUtils.join(",", symbols))
        val list = buildList {
            response.rates.entries.forEach { it ->
                add(ExchangeRate(response.base, it.key, it.value))
            }
        }
        dao.insertAll(list.map { ExchangeEntity(it.from, it.to, it.rate) })
        return list
    }

    override suspend fun addSymbols(symbols: Set<String>) {
        val current = preferences.getStringSet(SYMBOLS_KEY, mutableSetOf())!!
        val newSet = mutableSetOf<String>()
        newSet.addAll(current + symbols)
        preferences.edit().putStringSet(SYMBOLS_KEY, newSet).apply()
        _symbolFlow.send(newSet)
    }

    override suspend fun loadLocalSymbols() {
        addSymbols(mutableSetOf())
    }

    override suspend fun removeSymbol(symbol: String) {
        val rateSet = preferences.getStringSet(SYMBOLS_KEY, mutableSetOf())!!
        val newSet = rateSet.filter { it != symbol }.toSet()
        preferences.edit().putStringSet(SYMBOLS_KEY, newSet).apply()
        withContext(Dispatchers.IO) {
            dao.delete(ExchangeEntity("", symbol, 1.0))
        }
        _symbolFlow.send(newSet)
    }

    override suspend fun loadRemotePairs(): Map<String, String> {
        return service.loadRemotePairs()
    }

    override suspend fun loadLocalExchangeRates(): List<ExchangeRate> {
        return dao.getAll().map { ExchangeRate(it.from, it.to, it.rate) }
    }
}