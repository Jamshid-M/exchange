package com.example.exchange.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExchangeDao {

    @Query("SELECT * FROM exchangeentity")
    suspend fun getAll(): List<ExchangeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<ExchangeEntity>)

    @Delete
    fun delete(entity: ExchangeEntity)
}