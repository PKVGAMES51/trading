package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "register_data")
data class UserEntity(
    @PrimaryKey
    val username: String,
    val password: String,
    val name: String,
    val handphone: String,
    val email: String,
    val bank: String,
    val no_rekening: String,
    val refferal: String? = null,
    val saldo: Double = 0.0,
    val status: String = "active",
    val created_at: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val type: String, // 'deposit', 'withdraw'
    val amount: Double,
    val status: String = "pending", // 'pending', 'approved', 'rejected'
    val created_at: Long = System.currentTimeMillis()
)

@Entity(tableName = "trading_history")
data class TradingHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val pair_name: String,
    val trade_type: String, // 'buy', 'sell'
    val open_price: Double,
    val close_price: Double? = null,
    val amount: Double,
    val profit_loss: Double = 0.0,
    val status: String = "open", // 'open', 'closed'
    val created_at: Long = System.currentTimeMillis()
)
