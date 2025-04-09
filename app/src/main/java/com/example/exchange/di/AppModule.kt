package com.example.exchange.di

import android.content.Context
import androidx.room.Room
import com.example.exchange.data.local.AppDatabase
import com.example.exchange.data.remote.ExchangeService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideExchangeService(): ExchangeService {
        return Retrofit.Builder()
            .baseUrl("https://openexchangerates.org/api/")
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeService::class.java)
    }

    @Provides
    @Singleton
    fun provideRoomDB(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "exchange_db"
    ).build()

    @Provides
    @Singleton
    fun provideExchangeDao(appDatabase: AppDatabase) = appDatabase.exchangeDao()

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context) = context.getSharedPreferences("exchange_pref", Context.MODE_PRIVATE)

}