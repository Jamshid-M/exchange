package com.example.exchange.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchange.data.model.ExchangeRate
import com.example.exchange.domain.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: ExchangeRepository) : ViewModel() {

    private val REFRESH_TIMEOUT = 5000L
    private val _state = MutableStateFlow(HomeState(emptyList(), true))
    val state = _state.asStateFlow()
    private val channel = Channel<HomeEvent>()
    val event = channel.receiveAsFlow()

    init {
        combine(repository.symbolFlow, getCounterFlow()) { list, a ->
            list
        }
            .onEach {
                if (it.isNotEmpty()) {
                    try {
                        val result = repository.getExchangeRates()
                        _state.update { oldState ->
                            oldState.copy(
                                rates = result,
                                isLoading = false
                            )
                        }
                    } catch (e: Exception) {
                        loadLocalExchanges(e.message ?: "Something went wrong")
                        channel.send(HomeEvent.Error(e.message ?: "Something went wrong"))
                    }
                } else {
                    _state.update { oldState ->
                        oldState.copy(rates = emptyList(), isLoading = false)
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
        viewModelScope.launch {
            repository.loadLocalSymbols()
        }
    }

    private fun getCounterFlow() = flow {
        while (true) {
            emit(Unit)
            delay(REFRESH_TIMEOUT)
        }
    }

    fun removeSymbol(rate: ExchangeRate) {
        viewModelScope.launch {
            repository.removeSymbol(rate.to)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val result = repository.getExchangeRates()
                _state.update { oldState ->
                    oldState.copy(
                        rates = result,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { oldState ->
                    oldState.copy(error = e.message ?: "Something went wrong")
                }
                channel.send(HomeEvent.Error(e.message ?: "Something went wrong"))
            }
        }
    }

    private suspend fun loadLocalExchanges(error: String) {
        try {
            val result = repository.loadLocalExchangeRates()
            if (result.isNotEmpty()) {
                _state.update {
                    it.copy(rates = result, isLoading = false, error = null)
                }
            } else {
                _state.update {
                    it.copy(error = error)
                }
            }
        } catch (e: Exception) {
            _state.update { oldState ->
                oldState.copy(error = e.message ?: "Something went wrong")
            }
        }
    }
}