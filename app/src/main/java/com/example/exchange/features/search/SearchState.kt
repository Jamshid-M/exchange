package com.example.exchange.features.search

data class SearchState(
    val currencies: List<Pair<String, String>> = emptyList(),
    val searchList: List<Pair<String, String>>? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)