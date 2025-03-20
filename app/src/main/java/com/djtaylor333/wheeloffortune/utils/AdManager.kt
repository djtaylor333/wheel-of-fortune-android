package com.djtaylor333.wheeloffortune.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * Utility class for managing AdMob ads
 */
class AdManager(private val context: Context) {
    
    private val TAG = "AdManager"
    
    /**
     * Load a banner ad into the provided AdView
     * @param adView The AdView to load the ad into
     */
    fun loadBannerAd(adView: AdView) {
        val adRequest = AdRequest.Builder().build()
        
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "Ad loaded successfully")
            }
            
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Ad failed to load: ${adError.message}")
            }
            
            override fun onAdOpened() {
                Log.d(TAG, "Ad opened")
            }
            
            override fun onAdClosed() {
                Log.d(TAG, "Ad closed")
            }
        }
        
        adView.loadAd(adRequest)
    }
    
    companion object {
        // Banner ad unit IDs
        const val BANNER_AD_UNIT_ID = "ca-app-pub-0555936907173117/1234567890"
        
        // Test ad unit IDs for development
        const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        
        // Flag to use test ads during development
        const val USE_TEST_ADS = false
        
        /**
         * Get the appropriate ad unit ID based on build configuration
         */
        fun getBannerAdUnitId(): String {
            return if (USE_TEST_ADS) TEST_BANNER_AD_UNIT_ID else BANNER_AD_UNIT_ID
        }
    }
}
