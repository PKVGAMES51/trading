package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.SessionManager
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.trading.TradingViewModel
import com.example.ui.trading.DashboardScreen
import com.example.ui.trading.DepositScreen
import com.example.ui.trading.WithdrawScreen

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    
    val sessionManager = remember { SessionManager(context) }
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { AppRepository(database.tradingDao()) }
    
    val loggedInUser by sessionManager.loggedInUsername.collectAsState()
    
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repository, sessionManager) as T
            }
        }
    )

    val tradingViewModel: TradingViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TradingViewModel(repository, sessionManager) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = if (loggedInUser != null) "dashboard" else "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                viewModel = tradingViewModel,
                onDepositClick = { navController.navigate("deposit") },
                onWithdrawClick = { navController.navigate("withdraw") },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
        
        composable("deposit") {
            DepositScreen(
                viewModel = tradingViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("withdraw") {
            WithdrawScreen(
                viewModel = tradingViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
