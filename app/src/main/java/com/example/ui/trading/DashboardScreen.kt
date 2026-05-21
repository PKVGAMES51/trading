package com.example.ui.trading

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TradingHistoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TradingViewModel,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val saldo by viewModel.userSaldo.collectAsState()
    val price by viewModel.currentPrice.collectAsState()
    val pnl by viewModel.realtimeProfitLoss.collectAsState()
    val history by viewModel.marketHistory.collectAsState()
    val trades by viewModel.tradingHistory.collectAsState()

    var tradeAmount by remember { mutableStateOf("100") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trading Platform") },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Balance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Saldo Aktif", color = Color.Gray, fontSize = 14.sp)
                    Text("Rp ${String.format("%,.2f", saldo)}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Profit/Loss Aktif:")
                        Text(
                            text = if (pnl >= 0) "+Rp ${String.format("%,.2f", pnl)}" else "-Rp ${String.format("%,.2f", -pnl)}",
                            color = if (pnl >= 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = onDepositClick, modifier = Modifier.weight(1f)) {
                            Text("Deposit")
                        }
                        OutlinedButton(onClick = onWithdrawClick, modifier = Modifier.weight(1f)) {
                            Text("Withdraw")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Market Dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("BTC/USD", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("$${String.format("%,.2f", price)}", fontSize = 24.sp, color = if (price % 2 > 1) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Simulated Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF1E2329), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                ChartCanvas(history)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trading Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = tradeAmount,
                    onValueChange = { tradeAmount = it },
                    label = { Text("Nominal Trade") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.openTrade("buy", tradeAmount.toDoubleOrNull() ?: 0.0) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("BUY / Naik", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.openTrade("sell", tradeAmount.toDoubleOrNull() ?: 0.0) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("SELL / Turun", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { viewModel.closeAllTrades() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Tutup Semua Posisi", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Riwayat Trading / Posisi", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            trades.forEach { trade ->
                TradeItemRow(trade, viewModel)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TradeItemRow(trade: TradingHistoryEntity, viewModel: TradingViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("${trade.trade_type.uppercase()} ${trade.pair_name}", fontWeight = FontWeight.Bold, color = if (trade.trade_type == "buy") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary)
                Text(trade.status.uppercase(), color = if (trade.status == "open") Color.Yellow else Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Open: $${String.format("%,.2f", trade.open_price)} | Modal: Rp ${String.format("%,.2f", trade.amount)}")
            if (trade.status == "closed") {
                Text("Close: $${String.format("%,.2f", trade.close_price ?: 0.0)}")
                Text(
                    text = "PnL: ${if (trade.profit_loss >= 0) "+" else ""}${String.format("%,.2f", trade.profit_loss)}",
                    color = if (trade.profit_loss >= 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.closeTrade(trade) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Close Trade")
                }
            }
        }
    }
}

@Composable
fun ChartCanvas(history: List<MarketTick>) {
    if (history.isEmpty()) return

    val seriesColor = MaterialTheme.colorScheme.primary
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val maxPrice = history.maxOfOrNull { it.price } ?: 0.0
        val minPrice = history.minOfOrNull { it.price } ?: 0.0
        val range = (maxPrice - minPrice).coerceAtLeast(1.0)
        
        val path = Path()
        
        if (history.size > 1) {
            val stepX = width / (history.size - 1).coerceAtLeast(1).toFloat()
            
            history.forEachIndexed { index, tick ->
                val x = index * stepX
                val y = height - ((tick.price - minPrice) / range * height).toFloat()
                
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            
            drawPath(
                path = path,
                color = seriesColor,
                style = Stroke(width = 4f)
            )
        }
    }
}
