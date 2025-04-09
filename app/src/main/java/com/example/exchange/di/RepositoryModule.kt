package com.example.exchange.di

import com.example.exchange.domain.ExchangeRepository
import com.example.exchange.domain.ExchangeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindExchangeRepository(
        exchangeRepositoryImpl: ExchangeRepositoryImpl
    ): ExchangeRepository
}