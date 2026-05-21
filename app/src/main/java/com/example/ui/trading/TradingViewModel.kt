package com.example.ui.trading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.SessionManager
import com.example.data.TradingHistoryEntity
import com.example.data.TransactionEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class MarketTick(val price: Double, val time: Long)

class TradingViewModel(
    private val repository: AppRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val currentUsername = sessionManager.loggedInUsername.value ?: ""

    // User Data Flow
    val userSaldo = repository.observeSaldo(currentUsername)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val transactions = repository.observeTransactions(currentUsername)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tradingHistory = repository.observeTradingHistory(currentUsername)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val openTradesFlow = repository.observeOpenTrades(currentUsername)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Market Simulation Flow
    private val _currentPrice = MutableStateFlow(42000.50) // Starting BTC price
    val currentPrice = _currentPrice.asStateFlow()

    private val _marketHistory = MutableStateFlow<List<MarketTick>>(emptyList())
    val marketHistory = _marketHistory.asStateFlow()

    // Realtime Profit Loss calculation
    private val _realtimeProfitLoss = MutableStateFlow(0.0)
    val realtimeProfitLoss = _realtimeProfitLoss.asStateFlow()

    init {
        startMarketSimulation()
        observeOpenTradesForProfitLoss()
        startSaldoPolling()
    }

    private fun startSaldoPolling() {
        if (currentUsername.isEmpty()) return
        viewModelScope.launch {
            while (true) {
                try {
                    val response = com.example.data.RetrofitClient.apiService.checkSaldo(currentUsername)
                    if (response.success && response.saldo != null) {
                        val realSaldo = response.saldo.toDoubleOrNull() ?: 0.0
                        repository.updateSaldo(currentUsername, realSaldo)
                    }
                } catch (e: Exception) {
                    // Ignore network errors on polling
                }
                delay(5000)
            }
        }
    }

    private fun startMarketSimulation() {
        viewModelScope.launch {
            while (true) {
                delay(1000) // 1 second tick
                val volatility = Random.nextDouble(-50.0, 50.0)
                val newPrice = (_currentPrice.value + volatility).coerceAtLeast(100.0)
                _currentPrice.value = newPrice

                val currentList = _marketHistory.value.toMutableList()
                currentList.add(MarketTick(newPrice, System.currentTimeMillis()))
                if (currentList.size > 50) currentList.removeAt(0) // Keep last 50 ticks
                _marketHistory.value = currentList
            }
        }
    }

    private fun observeOpenTradesForProfitLoss() {
        viewModelScope.launch {
            openTradesFlow.collectLatest { openTrades ->
                while (true) {
                    val price = _currentPrice.value
                    var totalPnL = 0.0
                    for (trade in openTrades) {
                        val pnl = if (trade.trade_type == "buy") {
                            (price - trade.open_price) * (trade.amount / trade.open_price)
                        } else {
                            (trade.open_price - price) * (trade.amount / trade.open_price)
                        }
                        totalPnL += pnl
                    }
                    _realtimeProfitLoss.value = totalPnL
                    delay(500) // Update PnL every 500ms
                }
            }
        }
    }

    fun openTrade(type: String, amount: Double) {
        if (currentUsername.isEmpty() || amount <= 0) return
        val currentSaldo = userSaldo.value ?: 0.0
        if (currentSaldo < amount) return

        viewModelScope.launch {
            repository.updateSaldo(currentUsername, currentSaldo - amount)
            val trade = TradingHistoryEntity(
                username = currentUsername,
                pair_name = "BTC/USD",
                trade_type = type,
                open_price = _currentPrice.value,
                amount = amount
            )
            repository.insertTrade(trade)
        }
    }

    fun closeTrade(trade: TradingHistoryEntity) {
        viewModelScope.launch {
            val closePrice = _currentPrice.value
            val pnl = if (trade.trade_type == "buy") {
                (closePrice - trade.open_price) * (trade.amount / trade.open_price)
            } else {
                (trade.open_price - closePrice) * (trade.amount / trade.open_price)
            }
            
            val updatedTrade = trade.copy(
                status = "closed",
                close_price = closePrice,
                profit_loss = pnl
            )
            repository.updateTrade(updatedTrade)

            // Refund amount + pnl
            val current = repository.getUserByUsername(currentUsername)?.saldo ?: 0.0
            repository.updateSaldo(currentUsername, current + trade.amount + pnl)
        }
    }
    
    fun closeAllTrades() {
        viewModelScope.launch {
            val trades = openTradesFlow.value
            trades.forEach { closeTrade(it) }
        }
    }

    // Deposit / Withdraw Methods (using remote API)
    fun requestDeposit(amount: Double, method: String) {
        if (currentUsername.isEmpty() || amount <= 0) return
        viewModelScope.launch {
            try {
                val response = com.example.data.RetrofitClient.apiService.deposit(currentUsername, amount)
                if (response.success) {
                    // Update locally just for quick UI reaction, though real balance will sync
                    val current = repository.getUserByUsername(currentUsername)?.saldo ?: 0.0
                    repository.updateSaldo(currentUsername, current + amount)
                }
            } catch (e: Exception) {
                // Fallback or ignore
            }
        }
    }

    fun requestWithdraw(amount: Double) {
        if (currentUsername.isEmpty() || amount <= 0) return
        viewModelScope.launch {
            try {
                val current = repository.getUserByUsername(currentUsername)?.saldo ?: 0.0
                if (current >= amount) {
                    val response = com.example.data.RetrofitClient.apiService.withdraw(currentUsername, amount)
                    if (response.success) {
                        repository.updateSaldo(currentUsername, current - amount)
                    }
                }
            } catch (e: Exception) {
                // Fallback or ignore
            }
        }
    }
}
