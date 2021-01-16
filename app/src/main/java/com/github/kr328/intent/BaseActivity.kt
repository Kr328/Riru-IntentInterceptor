package com.github.kr328.intent

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.github.kr328.intent.compat.*

abstract class BaseActivity: AppCompatActivity() {
    private var contentView: ContentView? = null

    @Composable
    abstract fun Content()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.isSystemBarsTranslucentCompat = true

        applyDayNight()

        contentView = ContentView(this)

        contentView?.setContent {
            Content()
        }

        setContentView(contentView)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        applyDayNight(newConfig)
    }
    private fun applyDayNight(config: Configuration = resources.configuration) {
        if ((config.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            theme.applyStyle(R.style.AppThemeComposeDark, true)
        } else {
            theme.applyStyle(R.style.AppThemeComposeLight, true)
        }

        window.statusBarColor = resolveThemedColor(android.R.attr.statusBarColor)
        window.navigationBarColor = resolveThemedColor(android.R.attr.navigationBarColor)

        window.isLightStatusBarsCompat =
            resolveThemedBoolean(android.R.attr.windowLightStatusBar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            window.isLightNavigationBarCompat =
                resolveThemedBoolean(android.R.attr.windowLightNavigationBar)
        }

        contentView?.setConfiguration(configuration = config)
    }
}