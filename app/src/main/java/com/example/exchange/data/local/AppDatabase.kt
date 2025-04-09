package com.example.exchange.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ExchangeEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun exchangeDao(): ExchangeDao
}