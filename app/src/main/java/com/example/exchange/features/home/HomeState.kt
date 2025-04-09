package com.example.exchange.features.home

import com.example.exchange.data.model.ExchangeRate

data class HomeState(
    val rates: List<ExchangeRate>,
    val isLoading: Boolean,
    val error: String? = null
)

sealed class HomeEvent {
    data class Error(val message: String) : HomeEvent()
}