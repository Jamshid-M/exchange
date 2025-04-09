package com.example.exchange.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExchangeEntity(
    val from: String,
    @PrimaryKey val to: String,
    val rate: Double
)