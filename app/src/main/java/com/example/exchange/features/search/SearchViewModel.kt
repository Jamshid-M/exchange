package com.example.exchange.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchange.domain.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: ExchangeRepository) :
    ViewModel() {

    val symbols = mutableSetOf<String>()
    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()
    private val channel = Channel<String>()
    val errorEvent = channel.receiveAsFlow()

    init {
        loadRemoteData()
    }

    suspend fun onAddItemClicked() {
        repository.addSymbols(symbols)
    }

    fun loadRemoteData() {
        viewModelScope.launch {
            try {
                _state.update { oldState ->
                    oldState.copy(isLoading = true, error = null)
                }
                val res = repository.loadRemotePairs()
                val list = buildList {
                    res.entries.forEach { it ->
                        add(Pair(it.key, it.value))
                    }
                }
                _state.update { oldState ->
                    oldState.copy(currencies = list, isLoading = false, error = null)
                }
            } catch (e: Exception) {
                _state.update { oldState ->
                    oldState.copy(error = e.message)
                }
                channel.send(e.message ?: "Something went wrong")
            }
        }
    }

    fun searchPair(code: String) {
        if (code.isEmpty()) {
            _state.update {
                it.copy(searchList = null)
            }
        } else {
            _state.update {
                it.copy(searchList = it.currencies.filter { item -> item.first.contains(code.uppercase()) })
            }
        }
    }
}