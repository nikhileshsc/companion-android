package com.companion.astrodating.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.companion.astrodating.util.setFullscreenWithNavigation

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullscreenWithNavigation(this,window)
        initObservers()
        enableEdgeToEdge()

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true    // dark icons for light background
        insetsController.isAppearanceLightNavigationBars = true
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        val config = Configuration(base.resources.configuration).apply {
            if (fontScale > 1.0f) {
                densityDpi = base.resources.displayMetrics.xdpi.toInt()
            }
        }
        applyOverrideConfiguration(config)
    }

    abstract fun initObservers()
}