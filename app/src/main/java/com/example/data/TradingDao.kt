package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TradingDao {
    // User Queries
    @Query("SELECT * FROM register_data WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM register_data WHERE no_rekening = :accountNumber")
    suspend fun getUserByAccountNumber(accountNumber: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE register_data SET saldo = :newSaldo WHERE username = :username")
    suspend fun updateSaldo(username: String, newSaldo: Double)

    @Query("SELECT saldo FROM register_data WHERE username = :username")
    fun observeSaldo(username: String): Flow<Double?>

    // Transaction Queries
    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE username = :username ORDER BY created_at DESC")
    fun observeTransactions(username: String): Flow<List<TransactionEntity>>

    // Trading Queries
    @Insert
    suspend fun insertTrade(trade: TradingHistoryEntity)
    
    @Update
    suspend fun updateTrade(trade: TradingHistoryEntity)

    @Query("SELECT * FROM trading_history WHERE username = :username ORDER BY created_at DESC")
    fun observeTradingHistory(username: String): Flow<List<TradingHistoryEntity>>
    
    @Query("SELECT * FROM trading_history WHERE username = :username AND status = 'open' ORDER BY created_at DESC")
    fun observeOpenTrades(username: String): Flow<List<TradingHistoryEntity>>
}
