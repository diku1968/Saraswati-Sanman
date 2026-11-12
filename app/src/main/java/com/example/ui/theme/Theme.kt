package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = RegalGold,
  onPrimary = RoyalNavy,
  primaryContainer = RoyalBlue,
  onPrimaryContainer = RegalGoldLight,
  secondary = RegalGoldLight,
  onSecondary = RoyalNavy,
  tertiary = AccentBlue,
  background = Color(0xFF091427),
  surface = Color(0xFF0D1D38),
  onBackground = Color(0xFFF1F5F9),
  onSurface = Color(0xFFF1F5F9)
)

private val LightColorScheme = lightColorScheme(
  primary = RoyalNavy,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE5EEFC),
  onPrimaryContainer = RoyalNavy,
  secondary = RegalGold,
  onSecondary = RoyalNavy,
  secondaryContainer = GoldContainer,
  onSecondaryContainer = OnGoldContainer,
  tertiary = AccentBlue,
  onTertiary = Color.White,
  background = SurfaceWhite,
  onBackground = TextPrimary,
  surface = SurfaceCard,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceSubtle,
  onSurfaceVariant = TextSecondary,
  error = ErrorRed,
  onError = Color.White,
  errorContainer = ErrorBg,
  onErrorContainer = ErrorRed
)

@Composable
fun SaraswatiSammanTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
