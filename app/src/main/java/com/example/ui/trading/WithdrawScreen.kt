package com.example.ui.trading

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawScreen(
    viewModel: TradingViewModel,
    onBack: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    
    val saldo by viewModel.userSaldo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Withdraw Saldo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Saldo Aktif:")
                    Text(
                        "Rp ${String.format("%,.2f", saldo)}", 
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Nominal Penarikan") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (amt > 0 && amt <= (saldo ?: 0.0)) {
                        viewModel.requestWithdraw(amt)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Tarik Saldo")
            }
        }
    }
}
