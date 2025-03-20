package com.djtaylor333.wheeloffortune

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus

/**
 * Application class for initializing app-wide components like AdMob
 */
class WheelOfFortuneApplication : Application() {
    
    private val TAG = "WheelOfFortuneApp"
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize AdMob
        initializeAdMob()
    }
    
    private fun initializeAdMob() {
        // Set test device IDs to avoid invalid activity during testing
        val testDeviceIds = listOf("ABCDEF012345678901234567")
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
        MobileAds.setRequestConfiguration(configuration)
        
        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this) { initializationStatus ->
            val statusMap = initializationStatus.adapterStatusMap
            for ((adapter, status) in statusMap) {
                Log.d(TAG, "Adapter: $adapter Status: ${status.initializationState}")
            }
            Log.d(TAG, "AdMob SDK initialized successfully")
        }
    }
}
