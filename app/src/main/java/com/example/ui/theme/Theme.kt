package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFFCD535), // Binance Yellow
    onPrimary = Color(0xFF1E2329),
    secondary = Color(0xFF0ECB81), // Green for Buy/Profit
    onSecondary = Color.White,
    tertiary = Color(0xFFF6465D), // Red for Sell/Loss
    onTertiary = Color.White,
    background = Color(0xFF181A20),
    surface = Color(0xFF1E2329),
    onBackground = Color.White,
    onSurface = Color.White,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme
  dynamicColor: Boolean = false, // Disable dynamic colors to keep layout aesthetic
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
