package com.example.ui.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.domain.AppLanguage
import com.example.ui.theme.TextMuted
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdMobManager {
    private const val TAG = "AdMobManager"

    // Official Google AdMob sample test ad unit IDs
    const val BANNER_TEST_ID = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_TEST_ID = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private var lastAdShowTime = 0L
    private const val MIN_INTERVAL_BETWEEN_INTERSTITIALS_MS = 25_000L // 25s cooldown

    fun initialize(context: Context) {
        try {
            MobileAds.initialize(context) { status ->
                Log.d(TAG, "MobileAds initialized: ${status.adapterStatusMap}")
            }
            loadInterstitial(context)
        } catch (e: Exception) {
            Log.w(TAG, "AdMob initialization skipped/deferred: ${e.message}")
        }
    }

    fun loadInterstitial(context: Context) {
        if (isAdLoading || interstitialAd != null) return
        isAdLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                INTERSTITIAL_TEST_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d(TAG, "Interstitial ad loaded successfully")
                        interstitialAd = ad
                        isAdLoading = false
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "Interstitial failed to load: ${loadAdError.message}")
                        interstitialAd = null
                        isAdLoading = false
                    }
                }
            )
        } catch (e: Exception) {
            Log.w(TAG, "Interstitial loading exception: ${e.message}")
            isAdLoading = false
        }
    }

    fun showInterstitial(activity: Activity?, onAdClosed: () -> Unit) {
        if (activity == null) {
            onAdClosed()
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAdShowTime < MIN_INTERVAL_BETWEEN_INTERSTITIALS_MS) {
            // Cooldown protection
            onAdClosed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial dismissed")
                    interstitialAd = null
                    lastAdShowTime = System.currentTimeMillis()
                    loadInterstitial(activity)
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Interstitial failed to show: ${adError.message}")
                    interstitialAd = null
                    loadInterstitial(activity)
                    onAdClosed()
                }
            }
            ad.show(activity)
        } else {
            // Not ready yet, load for next opportunity
            loadInterstitial(activity)
            onAdClosed()
        }
    }
}

/**
 * Standard Material 3 AdMob Banner component for Jetpack Compose.
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    language: AppLanguage = AppLanguage.ENGLISH
) {
    val context = LocalContext.current
    var isAdFailed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Text(
                text = if (language == AppLanguage.GUJARATI) "જાહેરાત • Ad" else "Sponsored • Ad",
                fontSize = 9.sp,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }

        if (!isAdFailed) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admob_banner_view"),
                factory = { ctx ->
                    AdView(ctx).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = AdMobManager.BANNER_TEST_ID
                        adListener = object : AdListener() {
                            override fun onAdFailedToLoad(error: LoadAdError) {
                                super.onAdFailedToLoad(error)
                                isAdFailed = true
                            }
                        }
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        } else {
            // Fallback lightweight banner when offline or ad-blocking
            Surface(
                color = Color(0xFFE2E8F0),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (language == AppLanguage.GUJARATI) 
                            "સરસ્વતી સન્માન શિક્ષણ પુરસ્કાર પ્રણાલી" 
                            else "Saraswati Samman Award Education Network",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
