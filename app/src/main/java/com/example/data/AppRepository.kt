package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: TradingDao) {
    suspend fun getUserByUsername(username: String) = dao.getUserByUsername(username)
    suspend fun getUserByAccountNumber(accountNumber: String) = dao.getUserByAccountNumber(accountNumber)
    suspend fun insertUser(user: UserEntity) = dao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)
    suspend fun updateSaldo(username: String, newSaldo: Double) = dao.updateSaldo(username, newSaldo)
    
    fun observeSaldo(username: String): Flow<Double?> = dao.observeSaldo(username)
    
    suspend fun insertTransaction(transaction: TransactionEntity) = dao.insertTransaction(transaction)
    fun observeTransactions(username: String): Flow<List<TransactionEntity>> = dao.observeTransactions(username)
    
    suspend fun insertTrade(trade: TradingHistoryEntity) = dao.insertTrade(trade)
    suspend fun updateTrade(trade: TradingHistoryEntity) = dao.updateTrade(trade)
    fun observeTradingHistory(username: String): Flow<List<TradingHistoryEntity>> = dao.observeTradingHistory(username)
    fun observeOpenTrades(username: String): Flow<List<TradingHistoryEntity>> = dao.observeOpenTrades(username)
}
