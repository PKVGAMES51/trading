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
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    viewModel: TradingViewModel,
    onBack: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("QRIS") }
    
    val methods = listOf("QRIS", "Transfer Bank BCA")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deposit Saldo") },
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
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Nominal Deposit") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Pilih Metode:", modifier = Modifier.align(Alignment.Start))
            
            methods.forEach { m ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = (method == m),
                        onClick = { method = m }
                    )
                    Text(text = m, modifier = Modifier.padding(start = 8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (method == "Transfer Bank BCA") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Silakan transfer ke rekening berikut:", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("BANK BCA", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("0663153932", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("A/N SUPRIADI")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Deposit via QRIS akan otomatis diproses setelah Anda scan.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = { 
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    viewModel.requestDeposit(amt, method)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(if (method == "QRIS") "Lanjutkan ke QRIS" else "Saya Sudah Transfer")
            }
        }
    }
}
